package eval.code.quality.tests;

import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.stmt.*;

import eval.code.quality.position.MultiplePosition;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.ReportPosition;

import java.util.*;
import java.util.stream.Collectors;

public class Indentation extends CompilationUnitTest {
    private final Map<Integer, List<eval.code.quality.position.Position>> blockIndentations = new HashMap<>();

    public Indentation(ContentProvider contentProvider) {
        super(contentProvider);
    }

    @Override
    protected void testFor(ContentProvider contentProvider) {
        CompilationUnit compilationUnit = contentProvider.getCompilationUnit();
        compilationUnit.findAll(SwitchEntry.class).forEach(parent -> {
            NodeList<Statement> children = parent.getStatements();
            Range range = new Range(SinglePosition.from(children.get(0).getBegin().get()), SinglePosition.from(children.get(children.size() - 1).getBegin().get()));
            checkIndentationMap(getBlockIndentation(parent.getBegin().get().column, children), range, "block misaligned, expected all indented at one of: ");
        });
        compilationUnit.getImports().forEach(this::checkAlignLeft);
        compilationUnit.getPackageDeclaration().ifPresent(this::checkAlignLeft);
        compilationUnit.getTypes().forEach(this::checkAlignLeft);
        compilationUnit.getTypes().forEach(this::iterateTypeDeclaration);
    }

    @Override
    protected void afterTests() {
        if(blockIndentations.size() > 1) {
            Map<Integer, Integer> indentationCount = blockIndentations.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
            int maxNumberOfElement = Collections.max(indentationCount.values());
            List<Integer> goodIndentation = indentationCount.entrySet().stream().filter(e -> e.getValue() == maxNumberOfElement).map(Map.Entry::getKey).collect(Collectors.toList());
            List<Integer> wrongIndentation = indentationCount.entrySet().stream().filter(e -> e.getValue() != maxNumberOfElement).map(Map.Entry::getKey).collect(Collectors.toList());
            if (goodIndentation.size() > 1) {
                for (int i : goodIndentation) {
                    if (blockIndentations.get(i).size() > 1) {
                        MultiplePosition positions = new MultiplePosition();
                        blockIndentations.get(i).forEach(positions::add);
                        addError(ReportPosition.at(positions, "the same block difference for every blocks", "all of " + goodIndentation));
                    } else {
                        addError(ReportPosition.at(blockIndentations.get(i).get(0), "indentation of " + goodIndentation.get(0), i + ""));
                    }
                }
            }
            for (int i : wrongIndentation) {
                if (blockIndentations.get(i).size() > 1) {
                    MultiplePosition positions = new MultiplePosition();
                    blockIndentations.get(i).forEach(positions::add);
                    addError(ReportPosition.at(positions, "indentation of " + (goodIndentation.size() > 1 ? "one of " + goodIndentation : goodIndentation.get(0)), i + ""));
                } else {
                    addError(ReportPosition.at(blockIndentations.get(i).get(0), "indentation of " + (goodIndentation.size() > 1 ? "one of " + goodIndentation : goodIndentation.get(0)), i + ""));
                }
            }
        }
        blockIndentations.clear();
    }

    private void iterateTypeDeclaration(TypeDeclaration<?> typeDeclaration) {
        checkBlock(typeDeclaration, typeDeclaration.getMembers(), "multiple method and/or field declaration misaligned, expected all indented at one of: ");
        typeDeclaration.findAll(MethodDeclaration.class).forEach(this::visitChildStatement);
    }

    private void visitChildStatement(Node node) {
        List<Statement> children = getStatementChildren(node);
        if(!children.isEmpty() && !children.stream().allMatch(Statement::isBlockStmt)) {
            checkBlock(node, children.stream().filter(child -> !child.isBlockStmt()).collect(Collectors.toList()), "block misaligned, expected all indented at one of: ");
        }
        for(Statement child: children) {
            iterateBlock(node, child);
        }
    }

    private void iterateBlock(Node node, Statement child) {
        if(child.isBlockStmt()) {
            checkBlock(node, child.asBlockStmt().getStatements(), "block misaligned, expected all indented at one of: ");
            for(Statement c: child.asBlockStmt().getStatements()) {
                visitChildStatement(c);
            }
        } else {
            visitChildStatement(child);
        }
    }

    private List<Statement> getStatementChildren(Node node) {
        return node.getChildNodes().stream().filter(child -> child instanceof Statement).map(child -> (Statement)child).collect(Collectors.toList());
    }

    private void checkBlock(Node parent, List<? extends Node> children, String blockExpectation) {
        if(children.isEmpty()) {
            return;
        }
        Range range = new Range(SinglePosition.from(children.get(0).getBegin().get()), SinglePosition.from(children.get(children.size() - 1).getBegin().get()));
        checkIndentationMap(getBlockIndentation(parent.getBegin().get().column, children), range, blockExpectation);

    }

    public void checkIndentationMap(Map<Integer, List<eval.code.quality.position.Position>> indentationByDiff, Range blockRange, String blockExpectation) {
        if(indentationByDiff.size() > 1) {
            Map<Integer, Integer> indentationCount = indentationByDiff.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
            int maxNumberOfElement = Collections.max(indentationCount.values());
            List<Integer> goodIndentation = indentationCount.entrySet().stream().filter(e -> e.getValue() == maxNumberOfElement).map(Map.Entry::getKey).collect(Collectors.toList());
            List<Integer> wrongIndentation = indentationCount.entrySet().stream().filter(e -> e.getValue() != maxNumberOfElement).map(Map.Entry::getKey).collect(Collectors.toList());
            if(goodIndentation.size() > 1) {
                addError(ReportPosition.at(blockRange, blockExpectation + goodIndentation));
            } else {
                for(int i: wrongIndentation) {
                    if(indentationByDiff.get(i).size() > 1) {
                        MultiplePosition positions = new MultiplePosition();
                        indentationByDiff.get(i).forEach(positions::add);
                        addError(ReportPosition.at(positions, "indentation of " + goodIndentation.get(0), i + ""));
                    } else {
                        addError(ReportPosition.at(indentationByDiff.get(i).get(0), "indentation of " + goodIndentation.get(0), i + ""));
                    }
                }
                addToBlockIndentation(goodIndentation.get(0), blockRange);
            }
        } else {
            addToBlockIndentation(indentationByDiff.entrySet().iterator().next().getKey(), blockRange);
        }
    }

    private void checkAlignLeft(Node node) {
        node.getRange().ifPresent(e -> {
            Position position = e.begin;
            if(position.column != 1) {
                addError(ReportPosition.at(new SinglePosition(position.line, position.column), "element is not aligned left"));
            }
        });
    }

    private Map<Integer, List<eval.code.quality.position.Position>> getBlockIndentation(int parentIndentation, List<? extends Node> children) {
        List<Position> childrenPos = children.stream().map(e -> e.getBegin().get()).collect(Collectors.toList());
        Map<Integer, List<eval.code.quality.position.Position>> indentationByDiff = new HashMap<>();
        for(Position child: childrenPos) {
            if(child.column > parentIndentation) {
                addToMap(indentationByDiff, child.column - parentIndentation, child);
            } else {
                addError(ReportPosition.at(SinglePosition.from(child), "less indented or equally indented than parent"));
            }
        }
        return indentationByDiff;
    }

    private void addToBlockIndentation(int indentation, eval.code.quality.position.Position block) {
        if(blockIndentations.containsKey(indentation)) {
            List<eval.code.quality.position.Position> list = blockIndentations.get(indentation);
            list.add(block);
            blockIndentations.replace(indentation, list);
        } else {
            List<eval.code.quality.position.Position> list = new ArrayList<>();
            list.add(block);
            blockIndentations.put(indentation, list);
        }
    }

    private void addToMap(Map<Integer, List<eval.code.quality.position.Position>> map, int diff, Position child) {
        if(map.containsKey(diff)) {
            List<eval.code.quality.position.Position> list = map.get(diff);
            list.add(SinglePosition.from(child));
            map.replace(diff, list);
        } else {
            List<eval.code.quality.position.Position> list = new ArrayList<>();
            list.add(SinglePosition.from(child));
            map.put(diff, list);
        }
    }

    @Override
    protected String getName() {
        return "indentation";
    }
}
