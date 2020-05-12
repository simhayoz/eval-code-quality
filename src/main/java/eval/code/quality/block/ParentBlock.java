package eval.code.quality.block;

import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.stmt.*;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ParentBlock {

    public final Node parent;
    public final Range bracketPosition;
    public final List<? extends Node> childStatements;
    public final List<ChildBlock> childBlocks;

    protected ParentBlock(Node parent, List<? extends Node> childStatements, List<ChildBlock> childBlocks) {
        this(parent, null, childStatements, childBlocks);
    }

    protected ParentBlock(Node parent, Range bracketPosition, List<? extends Node> childStatements, List<ChildBlock> childBlocks) {
        this.parent = parent;
        this.bracketPosition = bracketPosition;
        this.childStatements = childStatements;
        this.childBlocks = childBlocks;
    }

    public Position getParentStart() {
        return parent.getBegin().get();
    }

    public int getParentLineEnd() {
        return parent.getBegin().get().line;
    }

    /**
     * Get all blocks in compilation unit as {@code ParentBlock}.
     *
     * @param compilationUnit the compilation unit to get the block from
     * @param content         the string content of the compilation unit
     * @return the list of all blocks
     */
    public static List<ParentBlock> getFor(CompilationUnit compilationUnit, String content) {
        List<ParentBlock> parentBlocks = new ArrayList<>();
        compilationUnit.findAll(AnnotationDeclaration.class).forEach(annotationDeclaration -> parentBlocks.add(new AnnotationBlock(annotationDeclaration)));
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classOrInterfaceDeclaration ->
                parentBlocks.add(new ClassOrInterfaceBlock(classOrInterfaceDeclaration)));
        compilationUnit.findAll(EnumDeclaration.class).forEach(enumDeclaration -> parentBlocks.add(new EnumBlock(enumDeclaration)));
        compilationUnit.findAll(SwitchEntry.class).forEach(switchEntry -> parentBlocks.add(new SwitchEntryBlock(switchEntry)));
        compilationUnit.findAll(BlockStmt.class).forEach(blockStmt -> blockStmt.getParentNode().ifPresent(parentNode -> {
            if (!(parentNode instanceof IfStmt)
                    && !(parentNode instanceof TryStmt)
                    && !(parentNode instanceof CatchClause)
                    && !(parentNode instanceof DoStmt)
                    && !(parentNode instanceof LambdaExpr)
                    && !(parentNode instanceof MethodDeclaration)) {
                parentBlocks.add(new Block(parentNode, blockStmt));
            }
            if(parentNode instanceof MethodDeclaration) {
                parentBlocks.add(new MethodBlock((MethodDeclaration) parentNode, blockStmt, content));
            }
        }));
        compilationUnit.findAll(TryStmt.class).forEach(tryStmt -> parentBlocks.add(new TryBlock(tryStmt, content)));
        compilationUnit.findAll(IfStmt.class).forEach(ifStmt -> {
            if (ifStmt.getParentNode().isEmpty() || !(ifStmt.getParentNode().get() instanceof IfStmt)) {
                parentBlocks.add(new IfBlock(ifStmt, content));
            }
        });
        compilationUnit.findAll(ForStmt.class).forEach(forStmt -> addToListIfOneLiner(parentBlocks, forStmt, forStmt));
        compilationUnit.findAll(ForEachStmt.class).forEach(forEachStmt -> addToListIfOneLiner(parentBlocks, forEachStmt, forEachStmt));
        compilationUnit.findAll(WhileStmt.class).forEach(whileStmt -> addToListIfOneLiner(parentBlocks, whileStmt, whileStmt));
        compilationUnit.findAll(DoStmt.class).forEach(doStmt -> parentBlocks.add(new DoBlock(doStmt, content)));
        compilationUnit.findAll(LambdaExpr.class).forEach(lambdaExpr -> {
            if (lambdaExpr.getBody().isBlockStmt()) {
                parentBlocks.add(new LambdaBlock(lambdaExpr, content));
            }
        });
        return parentBlocks;
    }

    private static void addToListIfOneLiner(List<ParentBlock> parentBlocks, NodeWithBody<?> nodeWithBody, Node node) {
        if (!nodeWithBody.getBody().isBlockStmt()) {
            parentBlocks.add(new ParentBlock(node, null, Collections.singletonList(nodeWithBody.getBody()), new ArrayList<>()));
        }
    }

    protected static Range getRangeOrNull(Statement statement) {
        if (statement.isBlockStmt()) {
            return Range.from(statement.asBlockStmt().getRange().get());
        }
        return null;
    }

    protected static List<Statement> getStatements(Statement statement) {
        if (statement.isBlockStmt()) {
            return statement.asBlockStmt().getStatements();
        }
        return Collections.singletonList(statement);
    }

    protected static SinglePosition getIndexNext(String content, String match, int fromLine) {
        Scanner scanner = new Scanner(content);
        int currentLine = 1;
        for (int i = 1; i < fromLine && scanner.hasNextLine(); ++i) {
            scanner.nextLine();
            currentLine++;
        }
        while (scanner.hasNextLine()) {
            int result = scanner.nextLine().indexOf(match);
            if (result != -1) {
                scanner.close();
                return new SinglePosition(currentLine, result + 1);
            }
            currentLine++;
        }
        scanner.close();
        return null;
    }

    protected static SinglePosition getIndexNext(String content, Pattern pattern, int fromLine) {
        List<String> contents = Arrays.asList(content.split(System.lineSeparator()));
        String smallerContent = contents.subList(fromLine-1, contents.size()).stream().collect(Collectors.joining(System.lineSeparator()));
        System.out.println(smallerContent);
        Matcher matcher = pattern.matcher(smallerContent);
        if(matcher.find()) {
            int after = matcher.end();
            Scanner scanner = new Scanner(content);
            int currentLine = 1;
            for (int i = 1; i < fromLine && scanner.hasNextLine(); ++i) {
                scanner.nextLine();
                currentLine++;
            }
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(after - line.length() <= 0) {
                    SinglePosition pos = new SinglePosition(currentLine, line.length() - after);
                    scanner.close();
                    return new SinglePosition(currentLine, after);
                } else {
                    after -= line.length() + 1;
                }
                currentLine++;
            }
            scanner.close();
        }
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
