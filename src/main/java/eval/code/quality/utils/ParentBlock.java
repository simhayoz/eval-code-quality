package eval.code.quality.utils;

import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.stmt.*;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;

import java.util.*;

public class ParentBlock {

    public final Node parent;
    public final Range bracketPosition;
    public final List<? extends Node> childStatements;
    public final List<ChildBlock> childBlocks;

    private ParentBlock(Node parent, List<? extends Node> childStatements, List<ChildBlock> childBlocks) {
        this(parent, null, childStatements, childBlocks);
    }

    private ParentBlock(Node parent, Range bracketPosition, List<? extends Node> childStatements, List<ChildBlock> childBlocks) {
        this.parent = parent;
        this.bracketPosition = bracketPosition;
        this.childStatements = childStatements;
        this.childBlocks = childBlocks;
    }

    public Position getParentStart() {
        return parent.getBegin().get();
    }

    public static List<ParentBlock> getFor(CompilationUnit compilationUnit, String content) {
        List<ParentBlock> parentBlocks = new ArrayList<>();
        compilationUnit.findAll(AnnotationDeclaration.class).forEach(annotationDeclaration ->
                parentBlocks.add(new ParentBlock(annotationDeclaration, annotationDeclaration.getMembers(), new ArrayList<>())));
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classOrInterfaceDeclaration ->
                parentBlocks.add(new ParentBlock(classOrInterfaceDeclaration, classOrInterfaceDeclaration.getMembers(), new ArrayList<>())));
        compilationUnit.findAll(EnumDeclaration.class).forEach(enumDeclaration ->
                parentBlocks.add(new ParentBlock(enumDeclaration, enumDeclaration.getEntries(), new ArrayList<>())));
        compilationUnit.findAll(SwitchEntry.class).forEach(parent -> {
            parentBlocks.add(new ParentBlock(parent, parent.getStatements(), new ArrayList<>()));
        });
        compilationUnit.findAll(BlockStmt.class).forEach(b -> b.getParentNode().ifPresent(parentNode -> {
            if(!(parentNode instanceof IfStmt)
                    && !(parentNode instanceof TryStmt)
                    && !(parentNode instanceof CatchClause)
                    && !(parentNode instanceof DoStmt)
                    && !(parentNode instanceof LambdaExpr)) {
                parentBlocks.add(new ParentBlock(parentNode instanceof BlockStmt ? b : parentNode, Range.from(b.getRange().get()), b.getStatements(), new ArrayList<>()));
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
                    childBlocks.add(new ChildBlock(getIndexNext(content, "else", getStartingLine(prev)),
                            getRangeOrNull(temp.getThenStmt()), getStatements(temp.getThenStmt())));
                    prev = temp;
                }
                final int tempLine = getStartingLine(temp);
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
                    Collections.singletonList(new ChildBlock(getIndexNext(content, "while", doStmt.getBegin().get().line), null, new ArrayList<>()))));
        });
        compilationUnit.findAll(LambdaExpr.class).forEach(lambdaExpr -> {
            if(lambdaExpr.getBody().isBlockStmt()) {
                lambdaExpr.getBegin().ifPresent(pos -> {
                    int columnStart = getIndexFirstElementLine(content, pos.line);
                    if(pos.column == columnStart) {
                        parentBlocks.add(new ParentBlock(lambdaExpr, getRangeOrNull(lambdaExpr.getBody().asBlockStmt()), getStatements(lambdaExpr.getBody().asBlockStmt()), new ArrayList<>()));
                    } else {
                        parentBlocks.add(
                                new ParentBlock(lambdaExpr, getRangeOrNull(lambdaExpr.getBody().asBlockStmt()), getStatements(lambdaExpr.getBody().asBlockStmt()), new ArrayList<>()) {
                                    @Override
                                    public Position getParentStart() {
                                        return Position.pos(pos.line, columnStart);
                                    }
                                });
                    }
                });
            }
        });
        return parentBlocks;
    }

    private static int getStartingLine(IfStmt ifStmt) {
        return ifStmt.getThenStmt().getEnd().get().line;
    }

    private static void addToListIfOneLiner(List<ParentBlock> parentBlocks, NodeWithBody<?> nodeWithBody, Node node) {
        if(!nodeWithBody.getBody().isBlockStmt()) {
            parentBlocks.add(new ParentBlock(node, null, Collections.singletonList(nodeWithBody.getBody()), new ArrayList<>()));
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
        for(int i = 1; i < fromLine && scanner.hasNextLine(); ++i) {
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

    private static int getIndexFirstElementLine(String content, int line) {
        String lineContent = content.split(System.lineSeparator())[line-1];
        return lineContent.indexOf(lineContent.trim()) + 1;
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
