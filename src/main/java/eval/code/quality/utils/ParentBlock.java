package eval.code.quality.utils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.stmt.*;
import eval.code.quality.position.Position;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;
import eval.code.quality.tests.BracketMatching;

import java.util.*;

public class ParentBlock {

    public final Node parent;
    public final Range bracketPosition;
    public final List<Statement> childStatements;
    public final List<ChildBlock> childBlocks;

    private ParentBlock(Node parent, List<Statement> childStatements, List<ChildBlock> childBlocks) {
        this(parent, null, childStatements, childBlocks);
    }

    private ParentBlock(Node parent, Range bracketPosition, List<Statement> childStatements, List<ChildBlock> childBlocks) {
        this.parent = parent;
        this.bracketPosition = bracketPosition;
        this.childStatements = childStatements;
        this.childBlocks = childBlocks;
    }

    public static List<ParentBlock> getFor(CompilationUnit compilationUnit, String content) {
        List<ParentBlock> parentBlocks = new ArrayList<>();
//        compilationUnit.getTypes().forEach(typeDeclaration -> {
////            checkBlock(typeDeclaration, typeDeclaration.getMembers(), "multiple method and/or field declaration misaligned, expected all indented at one of: ");
////            typeDeclaration.findAll(MethodDeclaration.class).forEach(this::visitChildStatement);
//            parentBlocks.add(new ParentBlock(typeDeclaration, typeDeclaration., typeDeclaration.getMembers(), null));
//        });
        compilationUnit.findAll(SwitchEntry.class).forEach(parent -> {
            parentBlocks.add(new ParentBlock(parent, parent.getStatements(), null));
        });
        compilationUnit.findAll(BlockStmt.class).forEach(b -> b.getParentNode().ifPresent(parentNode -> {
            if(!(parentNode instanceof IfStmt)
                    && !(parentNode instanceof TryStmt)
                    && !(parentNode instanceof CatchClause)
                    && !(parentNode instanceof DoStmt)) {
                parentBlocks.add(new ParentBlock(parentNode instanceof BlockStmt ? b : parentNode, Range.from(b.getRange().get()), b.getStatements(), null));// TODO isBlockStatement?
            }
        }));
        compilationUnit.findAll(TryStmt.class).forEach(tryStmt -> {
            List<ChildBlock> childBlocks = new ArrayList<>();
            for(CatchClause catchClause: tryStmt.getCatchClauses()) {
                childBlocks.add(new ChildBlock(SinglePosition.from(catchClause.getBegin().get()), Range.from(catchClause.getBody().getRange().get()), catchClause.getBody().getStatements()));
            }
            tryStmt.getFinallyBlock().ifPresent(finalElement ->
                    childBlocks.add(new ChildBlock(getIndexNext(content, "finally", tryStmt.getBegin().get().line),
                            Range.from(finalElement.getRange().get()),
                            finalElement.getStatements())));
            parentBlocks.add(new ParentBlock(tryStmt, Range.from(tryStmt.getTryBlock().getRange().get()), tryStmt.getTryBlock().getStatements(), childBlocks));
        });
        compilationUnit.findAll(IfStmt.class).forEach(ifStmt -> {
            if(ifStmt.getParentNode().isEmpty() || !(ifStmt.getParentNode().get() instanceof IfStmt)) {
                List<ChildBlock> childBlocks = new ArrayList<>();
                IfStmt temp = ifStmt;
                IfStmt prev = ifStmt;
                while(temp.hasCascadingIfStmt()) {
                    temp = temp.getElseStmt().get().asIfStmt();
                    childBlocks.add(new ChildBlock(getIndexNext(content, "else", prev.getBegin().get().line),
                            getRangeOrNull(temp.getThenStmt()), getStatements(temp.getThenStmt())));
                    prev = temp;
                }
                final int tempLine = temp.getBegin().get().line;
                temp.getElseStmt().ifPresent(elseBranch -> childBlocks.add(new ChildBlock(getIndexNext(content, "else", tempLine),
                        getRangeOrNull(elseBranch), getStatements(elseBranch))));
                parentBlocks.add(new ParentBlock(ifStmt, getRangeOrNull(ifStmt.getThenStmt()),
                        getStatements(ifStmt.getThenStmt()), childBlocks));
            }
        });
        compilationUnit.findAll(ForStmt.class).forEach(forStmt -> addToListIfOneLiner(parentBlocks, forStmt, forStmt));
        compilationUnit.findAll(ForEachStmt.class).forEach(forEachStmt -> addToListIfOneLiner(parentBlocks, forEachStmt, forEachStmt));
        compilationUnit.findAll(WhileStmt.class).forEach(whileStmt -> addToListIfOneLiner(parentBlocks, whileStmt, whileStmt));
        compilationUnit.findAll(DoStmt.class).forEach(doStmt -> {
            parentBlocks.add(new ParentBlock(doStmt,
                    getRangeOrNull(doStmt.getBody()),
                    getStatements(doStmt.getBody()),
                    Collections.singletonList(new ChildBlock(getIndexNext(content, "while", doStmt.getBegin().get().line), null, null))));
        });
        return parentBlocks;
    }

    private static void addToListIfOneLiner(List<ParentBlock> parentBlocks, NodeWithBody<?> nodeWithBody, Node node) {
        if(!nodeWithBody.getBody().isBlockStmt()) {
            parentBlocks.add(new ParentBlock(node, null, Collections.singletonList(nodeWithBody.getBody()), null));
        }
    }

    private static Range getRangeOrNull(Statement statement) {
        if(statement.isBlockStmt()) {
            return Range.from(statement.asBlockStmt().getRange().get());
        }
        return null;
    }

    private static List<Statement> getStatements(Statement statement) {
        if(statement.isBlockStmt()) {
            return statement.asBlockStmt().getStatements();
        }
        return Collections.singletonList(statement);
    }

    private static SinglePosition getIndexNext(String content, String match, int fromLine) {
        Scanner scanner = new Scanner(content);
        int currentLine = 1;
        for(int i = 0; i < fromLine && scanner.hasNextLine(); ++i) {
            scanner.nextLine();
            currentLine++;
        }
        while(scanner.hasNextLine()) {
            int result = scanner.nextLine().indexOf(match);
            if(result != -1) {
                scanner.close();
                return new SinglePosition(currentLine, result + 1);
            }
            currentLine++;
        }
        scanner.close();
        return null;
    }

    @Override
    public String toString() {
        return "ParentBlock{\n" +
                ("parent=" + parent + System.lineSeparator() +
                        ("bracketPosition=" + bracketPosition).indent(2) + System.lineSeparator() +
                        ("childStatements=" + childStatements).indent(2) + System.lineSeparator() +
                        ("childBlocks=" + childBlocks).indent(2)).indent(2) + System.lineSeparator() +
                '}';
    }
}
