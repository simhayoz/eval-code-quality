package eval.code.quality.checks;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import eval.code.quality.block.ChildBlock;
import eval.code.quality.block.ParentBlock;
import eval.code.quality.position.*;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.description.Description;
import eval.code.quality.utils.description.DescriptionBuilder;
import eval.code.quality.utils.description.Descriptor;
import eval.code.quality.utils.reporter.ExpectedReporter;
import eval.code.quality.utils.reporter.NotExpectedReporter;

import java.util.*;

/**
 * Check Indentation for a {@code CompilationUnit}.
 * <p>
 * Checks for:
 * <ul>
 * <li>Same indentation for each statements within the same block
 * <li>Greater indentation than parent for each statement inside a block
 * <li>Blocks have the same indentation difference everywhere
 * </ul>
 */
public class Indentation extends CompilationUnitCheck {
    private final Map<Integer, List<Position>> blockIndentations = new HashMap<>();

    public Indentation(ContentProvider contentProvider) {
        super(contentProvider);
    }

    @Override
    protected void checkFor(ContentProvider contentProvider) {
        CompilationUnit compilationUnit = contentProvider.getCompilationUnit();
        compilationUnit.getImports().forEach(this::checkAlignLeft);
        compilationUnit.getPackageDeclaration().ifPresent(this::checkAlignLeft);
        compilationUnit.getTypes().forEach(this::checkAlignLeft);
        ParentBlock.getFor(compilationUnit, contentProvider.getString()).forEach(parentBlock -> {
            checkBlock(parentBlock, parentBlock.childStatements);
            for(ChildBlock childBlock: parentBlock.childBlocks) {
                checkBlock(parentBlock, childBlock.childStatements);
            }
        });
    }

    @Override
    protected void afterChecks() {
        inferMapProperty.checkAndReport(blockIndentations, true);
    }

    private void checkAlignLeft(Node node) {
        node.getRange().ifPresent(e -> {
            SinglePosition position = SinglePosition.from(e.begin);
            if (e.begin.column != 1) {
                addError(new DescriptionBuilder()
                        .addPosition(context.getPos(position), new Descriptor().addToDescription("element is not aligned left")));
            }
        });
    }

    private void checkBlock(ParentBlock parentBlock, List<? extends Node> children) {
        if (!children.isEmpty()) {
            Position range = context.getRange(children);
            checkIndentationMap(getIndentationMap(parentBlock.getParentStart(), children), range);
        }
    }

    private Map<Integer, List<Position>> getIndentationMap(com.github.javaparser.Position parent, List<? extends Node> children) {
        Map<Integer, List<Position>> indentationByDiff = new HashMap<>();
        for (Node child : children) {
            child.getBegin().ifPresent(childPos -> {
                int diff = childPos.column - parent.column;
                if (diff > 0) {
                    if(childPos.line != parent.line) {
                        addToMap(indentationByDiff, diff, context.getPos(childPos));
                    }
                } else {
                    addError(new DescriptionBuilder()
                            .addPosition(context.getPos(child), new Descriptor().addToDescription("less indented or equally indented than parent")));
                }
            });
        }
        return indentationByDiff;
    }

    public void checkIndentationMap(Map<Integer, List<Position>> indentationByDiff, Position blockRange) {
        inferMapProperty.checkAndReport(indentationByDiff, new ExpectedReporter<>() {
            @Override
            public void doOnUniqueExpected(Map<Integer, List<Position>> map, Integer property) {
                addToBlockIndentation(property, blockRange);
            }

            @Override
            public Description reportMultipleExpected(Map<Integer, List<Position>> map, List<Integer> properties) {
                return new DescriptionBuilder().addPosition(blockRange, new Descriptor().setExpected("all indented at one of: " + properties.toString()).addToDescription("block misaligned")).build();
            }
        }, new NotExpectedReporter<>(), false);
        if (indentationByDiff.size() == 1) {
            addToBlockIndentation(indentationByDiff.entrySet().iterator().next().getKey(), blockRange);
        }
    }

    private void addToBlockIndentation(int indentation, Position block) {
        addToMap(blockIndentations, indentation, block);
    }

    private void addToMap(Map<Integer, List<Position>> map, int diff, Position child) {
        if (map.containsKey(diff)) {
            map.get(diff).add(child);
        } else {
            List<Position> list = new ArrayList<>();
            list.add(child);
            map.put(diff, list);
        }
    }

    @Override
    public String getName() {
        return "indentation";
    }
}