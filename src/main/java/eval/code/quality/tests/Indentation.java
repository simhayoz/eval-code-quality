package eval.code.quality.tests;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import eval.code.quality.position.*;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.*;
import eval.code.quality.utils.Error;

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
public class Indentation extends CompilationUnitTest {
    private final Map<Integer, List<Position>> blockIndentations = new HashMap<>();

    public Indentation(ContentProvider contentProvider) {
        super(contentProvider);
    }

    @Override
    protected void testFor(ContentProvider contentProvider) {
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
    protected void afterTests() {
        checkAndReport(blockIndentations, true);
    }

    private void checkAlignLeft(Node node) {
        node.getRange().ifPresent(e -> {
            SinglePosition position = SinglePosition.from(e.begin);
            if (e.begin.column != 1) {
                addError(ReportPosition.at(context.getPos(position), "element is not aligned left"));
            }
        });
    }

    private void checkBlock(ParentBlock parentBlock, List<? extends Node> children) {
        if (!children.isEmpty()) {
            Position range = context.getRange(children);
            checkIndentationMap(getIndentationMap(parentBlock.getParentStart(), children), range, "block misaligned, expected all indented at one of: ");
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
                    addError(ReportPosition.at(context.getPos(child), "less indented or equally indented than parent"));
                }
            });
        }
        return indentationByDiff;
    }

    public void checkIndentationMap(Map<Integer, List<Position>> indentationByDiff, Position blockRange, String blockExpectation) {
        checkAndReport(indentationByDiff, new ExpectedReporter<>() {
            @Override
            public void doOnUniqueExpected(Map<Integer, List<Position>> map, Integer property) {
                addToBlockIndentation(property, blockRange);
            }

            @Override
            public Error reportMultipleExpected(Map<Integer, List<Position>> map, List<Integer> properties) {
                return ReportPosition.at(blockRange, blockExpectation + properties);
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
    protected String getName() {
        return "indentation";
    }
}