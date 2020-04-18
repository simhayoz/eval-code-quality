package eval.code.quality.tests;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;

import eval.code.quality.position.*;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.MultiplePossibility;
import eval.code.quality.utils.ReportPosition;

import java.util.*;
import java.util.stream.Collectors;

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
        compilationUnit.getTypes().forEach(typeDeclaration -> {
            checkBlock(typeDeclaration, typeDeclaration.getMembers(), "multiple method and/or field declaration misaligned, expected all indented at one of: ");
            typeDeclaration.findAll(MethodDeclaration.class).forEach(this::visitChildStatement);
        });
        compilationUnit.findAll(SwitchEntry.class).forEach(parent -> {
            NodeList<Statement> children = parent.getStatements();
            Position range = context.getRange(children);
            checkIndentationMap(getIndentationMap(parent.getBegin().get().column, children), range, "block misaligned, expected all indented at one of: ");
        });
    }

    @Override
    protected void afterTests() {
        if (blockIndentations.size() <= 1) {
            blockIndentations.clear();
            return;
        }
        Map<Integer, Integer> indentationCount = blockIndentations.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
        int maxNumberOfElement = Collections.max(indentationCount.values());
        List<Integer> goodIndentation = new ArrayList<>();
        List<Integer> wrongIndentation = new ArrayList<>();
        indentationCount.forEach((key, value) -> (value == maxNumberOfElement ? goodIndentation : wrongIndentation).add(key));
        if (goodIndentation.size() > 1) {
            Map<Position, String> intended = new HashMap<>();
            goodIndentation.forEach(i -> intended.put(
                    (blockIndentations.get(i).size() > 1 ? new MultiplePosition(blockIndentations.get(i)) : blockIndentations.get(i).get(0)), i + ""));
            addError(MultiplePossibility.at(intended, "Multiple possible indentation for blocks, should be all the same"));
        }
        reportWrongIndentationBlock(wrongIndentation, goodIndentation);
    }

    private void reportWrongIndentationBlock(List<Integer> wrongIndentation, List<Integer> goodIndentation) {
        for (int i : wrongIndentation) {
            if (blockIndentations.get(i).size() > 1) {
                MultiplePosition positions = new MultiplePosition();
                blockIndentations.get(i).forEach(positions::add);
                addError(ReportPosition.at(positions, "difference indentation of " + goodIndentation.get(0), i + ""));
            } else {
                addError(ReportPosition.at(blockIndentations.get(i).get(0), "difference indentation of " + goodIndentation.get(0), i + ""));
            }
        }
    }

    private void checkAlignLeft(Node node) {
        node.getRange().ifPresent(e -> {
            SinglePosition position = SinglePosition.from(e.begin);
            if (position.column.get() != 1) {
                addError(ReportPosition.at(context.getPos(position), "element is not aligned left"));
            }
        });
    }

    private void checkBlock(Node parent, List<? extends Node> children, String errorMessage) {
        if (children.isEmpty()) {
            return;
        }
        Position range = context.getRange(children);
        checkIndentationMap(getIndentationMap(parent.getBegin().get().column, children), range, errorMessage);
    }

    private void visitChildStatement(Node node) {
        List<Statement> children = node.getChildNodes().stream().filter(child -> child instanceof Statement).map(child -> (Statement) child).collect(Collectors.toList());
        if (!children.isEmpty() && !children.stream().allMatch(Statement::isBlockStmt)) {
            checkBlock(node, children, "block misaligned, expected all indented at one of: ");
        }
        for (Statement child : children) {
            iterateBlock(node, child);
        }
    }

    private void iterateBlock(Node node, Statement child) {
        if (child.isBlockStmt()) {
            checkBlock(node, child.asBlockStmt().getStatements(), "block misaligned, expected all indented at one of: ");
            for (Statement c : child.asBlockStmt().getStatements()) {
                visitChildStatement(c);
            }
        } else {
            visitChildStatement(child);
        }
    }

    /**
     * Get a map of indentation -> list of position at that indentation.
     * Also check the indentation for each child
     */
    private Map<Integer, List<Position>> getIndentationMap(int parentIndentation, List<? extends Node> children) {
        Map<Integer, List<Position>> indentationByDiff = new HashMap<>();
        for (Node child : children) {
            int column = child.getBegin().get().column;
            if (column > parentIndentation) {
                addToMap(indentationByDiff, column - parentIndentation, SinglePosition.from(child.getBegin().get()));
            } else {
                addError(ReportPosition.at(context.getPos(child), "less indented or equally indented than parent"));
            }
        }
        return indentationByDiff;
    }

    public void checkIndentationMap(Map<Integer, List<Position>> indentationByDiff, Position blockRange, String blockExpectation) {
        if (indentationByDiff.size() > 1) {
            Map<Integer, Integer> indentationCount = indentationByDiff.entrySet().stream().collect(
                    Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
            int maxNumberOfElement = Collections.max(indentationCount.values());
            List<Integer> goodIndentation = new ArrayList<>();
            List<Integer> wrongIndentation = new ArrayList<>();
            indentationCount.forEach((key, value) -> (value == maxNumberOfElement ? goodIndentation : wrongIndentation).add(key));
            if (goodIndentation.size() > 1) {
                addError(ReportPosition.at(blockRange, blockExpectation + goodIndentation));
            } else {
                reportWrongIndentation(goodIndentation, wrongIndentation, indentationByDiff);
                addToBlockIndentation(goodIndentation.get(0), blockRange);
            }
        } else if(indentationByDiff.size() == 1) { // TODO make only else when fixed
            addToBlockIndentation(indentationByDiff.entrySet().iterator().next().getKey(), blockRange);
        }
    }

    private void reportWrongIndentation(List<Integer> goodIndentation, List<Integer> wrongIndentation, Map<Integer, List<Position>> indentationByDiff) {
        for (int i : wrongIndentation) {
            if (indentationByDiff.get(i).size() > 1) {
                MultiplePosition positions = new MultiplePosition();
                indentationByDiff.get(i).forEach(positions::add);
                addError(ReportPosition.at(context.getPos(positions), "indentation of " + goodIndentation.get(0), i + ""));
            } else {
                addError(ReportPosition.at(context.getPos(indentationByDiff.get(i).get(0)), "indentation of " + goodIndentation.get(0), i + ""));
            }
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
