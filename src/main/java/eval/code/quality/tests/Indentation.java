package eval.code.quality.tests;

import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.stmt.*;
import eval.code.quality.position.MultiplePosition;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.DifferencePosition;
import eval.code.quality.utils.MultiplePossibility;
import eval.code.quality.utils.ReportPosition;

import java.util.*;
import java.util.stream.Collectors;

public class Indentation extends CompilationUnitTest {
    private final Map<Integer, List<Range>> blockIndentations = new HashMap<>();

    public Indentation(ContentProvider contentProvider) {
        super(contentProvider);
    }

    @Override
    protected void test() {
        super.test();
        System.out.println(blockIndentations);
        if(blockIndentations.size() > 1) {
            // TODO test block diff
        }
    }

    @Override
    protected void testFor(String content, CompilationUnit compilationUnit) {
        compilationUnit.getImports().forEach(this::checkAlignLeft);
        compilationUnit.getPackageDeclaration().ifPresent(this::checkAlignLeft);
        compilationUnit.getTypes().forEach(this::checkAlignLeft);
        compilationUnit.getTypes().forEach(this::iterateTypeDeclaration);
    }

    private void iterateTypeDeclaration(TypeDeclaration<?> typeDeclaration) {
        checkBlockType(typeDeclaration, typeDeclaration.getMembers());
        typeDeclaration.findAll(MethodDeclaration.class).forEach(this::visitChildStatement);
    }

    private void visitChildStatement(Node node) {
        List<Statement> children = getStatementChildren(node);
        if(!children.isEmpty() && !children.stream().allMatch(Statement::isBlockStmt)) {
            checkBlock(node, children.stream().filter(child -> !child.isBlockStmt()).collect(Collectors.toList()));
        }
        for(Statement child: children) {
            if(child.isBlockStmt()) {
                checkBlock(node, child.asBlockStmt().getStatements());
                for(Statement c: child.asBlockStmt().getStatements()) {
                    visitChildStatement(c);
                }
            } else {
                visitChildStatement(child);
            }
        }
    }

    private List<Statement> getStatementChildren(Node node) {
        return node.getChildNodes().stream().filter(child -> child instanceof Statement).map(child -> (Statement)child).collect(Collectors.toList());
    }

    private void checkBlock(Node parent, List<Statement> children) {
        if(children.isEmpty()) {
            return;
        }
        Range range = new Range(SinglePosition.from(children.get(0).getBegin().get()), SinglePosition.from(children.get(children.size() - 1).getBegin().get()));
        Map<Integer, List<eval.code.quality.position.Position>> indentationByDiff = getBlockIndentation(parent.getBegin().get().column, children);
        if(indentationByDiff.size() > 1) {
            Map<Integer, Integer> indentationCount = indentationByDiff.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
            int maxNumberOfElement = Collections.max(indentationCount.values());
            List<Integer> goodIndentation = indentationCount.entrySet().stream().filter(e -> e.getValue() == maxNumberOfElement).map(Map.Entry::getKey).collect(Collectors.toList());
            if(goodIndentation.size() > 1) {
                addError(ReportPosition.at(range, "block misaligned, expected indented at one of: " + goodIndentation));
            } else {
                addError(ReportPosition.at(range, "block misaligned, expected indented at: " + goodIndentation.get(0)));
                addToBlockIndentation(goodIndentation.get(0), range);
            }
        } else {
            addToBlockIndentation(indentationByDiff.entrySet().iterator().next().getKey(), range);
        }
    }

    private void checkBlockType(TypeDeclaration<?> parent, NodeList<BodyDeclaration<?>> children) {
        System.out.println("Will do a check for:");
        System.out.println("Parent: ");
        System.out.println(parent.toString().indent(2));
        System.out.println("Children: ");
        children.forEach(e -> System.out.println(e.toString().indent(4)));
        System.out.println("-----------------------------------------------");
        // TODO this
    }

    private void checkAlignLeft(Node node) {
        node.getRange().ifPresent(e -> {
            Position position = e.begin;
            if(position.column != 1) {
                addError(ReportPosition.at(new SinglePosition(position.line, position.column), "element is not aligned left"));
            }
        });
    }

    private Map<Integer, List<eval.code.quality.position.Position>> getBlockIndentation(int parentIndentation, List<Statement> children) {
        Map<Integer, List<eval.code.quality.position.Position>> indentationByDiff = new HashMap<>();
        for(Statement child: children) {
            if(child.getBegin().get().column > parentIndentation) {
                int diff = child.getBegin().get().column - parentIndentation;
                if(indentationByDiff.containsKey(diff)) {
                    List<eval.code.quality.position.Position> list = indentationByDiff.get(diff);
                    list.add(SinglePosition.from(child.getBegin().get()));
                    indentationByDiff.replace(diff, list);
                } else {
                    List<eval.code.quality.position.Position> list = new ArrayList<>();
                    list.add(SinglePosition.from(child.getBegin().get()));
                    indentationByDiff.put(diff, list);
                }
            } else {
                addError(ReportPosition.at(new SinglePosition(child.getBegin().get().line, child.getBegin().get().column), "less indented or equally indented than parent"));
            }
        }
        return indentationByDiff;
    }

    private void addToBlockIndentation(int indentation, Range block) {
        if(blockIndentations.containsKey(indentation)) {
            List<Range> list = blockIndentations.get(indentation);
            list.add(block);
            blockIndentations.replace(indentation, list);
        } else {
            List<Range> list = new ArrayList<>();
            list.add(block);
            blockIndentations.put(indentation, list);
        }
    }

    @Override
    protected String getName() {
        return "indentation";
    }
}
