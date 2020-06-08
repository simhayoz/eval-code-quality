package eval.code.quality.block;

import com.github.javaparser.JavaToken;
import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.stmt.*;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents a parent block.
 */
public class ParentBlock {

    public final Node parent;
    public final Range bracesPosition;
    public final List<? extends Node> childStatements;
    public final List<ChildBlock> childBlocks;

    protected ParentBlock(Node parent, List<? extends Node> childStatements, List<ChildBlock> childBlocks) {
        this(parent, null, childStatements, childBlocks);
    }

    protected ParentBlock(Node parent, Range bracesPosition, List<? extends Node> childStatements, List<ChildBlock> childBlocks) {
        this.parent = parent;
        this.bracesPosition = bracesPosition;
        this.childStatements = childStatements;
        this.childBlocks = childBlocks;
    }

    /**
     * Get the parent start position.
     *
     * @return the parent start position
     */
    public Position getParentStart() {
        return parent.getBegin().get();
    }

    /**
     * Get the parent end line (header end of line).
     *
     * @return the parent end line
     */
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
                    && !(parentNode instanceof CallableDeclaration)) {
                parentBlocks.add(new Block(parentNode, blockStmt));
            }
            if (parentNode instanceof CallableDeclaration) {
                parentBlocks.add(new CallableBlock((CallableDeclaration<?>) parentNode, blockStmt, content));
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

    /**
     * Get the range of the block or null, if the current statement is not a {@code BlockStmt}.
     *
     * @param statement the statement to get the range from
     * @return the range of the block or null
     */
    protected static Range getRangeOrNull(Statement statement) {
        if (statement.isBlockStmt()) {
            return Range.from(statement.asBlockStmt().getRange().get());
        }
        return null;
    }

    /**
     * Get statements inside the block.
     *
     * @param statement the statement to extract the statements from
     * @return statements inside the block
     */
    protected static List<Statement> getStatements(Statement statement) {
        if (statement.isBlockStmt()) {
            return statement.asBlockStmt().getStatements();
        }
        return Collections.singletonList(statement);
    }

    /**
     * Get the index of the next matching string from line {@code fromLine} inside {@code content}.
     *
     * @param content  the content to find the next matching string from
     * @param match    the string to match with content
     * @param position the starting position for the search
     * @return the index of the next matching string from line {@code fromLine} inside {@code content}
     */
    public static SinglePosition getIndexNext(String content, String match, SinglePosition position) {
        try(Scanner scanner = new Scanner(content)) {
            String lineContent;
            for (int i = 1; scanner.hasNextLine(); ++i) {
                if (i < position.line) {
                    scanner.nextLine();
                } else {
                    int columnDiff = 0;
                    if (i == position.line) {
                        lineContent = scanner.nextLine().substring(position.column.orElse(0));
                        columnDiff = position.column.orElse(0);
                    } else {
                        lineContent = scanner.nextLine();
                    }
                    int result = lineContent.indexOf(match);
                    if (result != -1) {
                        scanner.close();
                        return new SinglePosition(i, columnDiff + result + 1);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get the index of the next matching pattern from line {@code fromLine} inside {@code content}.
     *
     * @param content  the content to find the next matching pattern from
     * @param pattern  the pattern to find
     * @param fromLine starting line of the search
     * @return the index of the next matching pattern from line {@code fromLine} inside {@code content}
     */
    protected static SinglePosition getIndexNext(String content, Pattern pattern, int fromLine) {
        List<String> contents = Arrays.asList(content.split(System.lineSeparator()));
        String smallerContent = contents.subList(fromLine - 1, contents.size()).stream().collect(Collectors.joining(System.lineSeparator()));
        Matcher matcher = pattern.matcher(smallerContent);
        if (matcher.find()) {
            int after = matcher.end();
            Scanner scanner = new Scanner(content);
            int currentLine = 1;
            for (int i = 1; i < fromLine && scanner.hasNextLine(); ++i) {
                scanner.nextLine();
                currentLine++;
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (after - line.length() <= 0) {
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

    /**
     * Get braces range from body declaration.
     *
     * @param node the body declaration
     * @return braces range from body declaration
     */
    protected static Range getRangeFromBodyDeclaration(BodyDeclaration<?> node) {
        SinglePosition end = SinglePosition.from(node.getEnd().get());
        for (JavaToken token : node.getTokenRange().get()) {
            if (token.asString().equals("{")) {
                return new Range(SinglePosition.from(token.getRange().get().begin), end);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ParentBlock{\n" +
                ("parent=" + parent + System.lineSeparator() +
                        ("bracesPosition=" + bracesPosition).indent(2) + System.lineSeparator() +
                        ("childStatements=" + childStatements).indent(2) + System.lineSeparator() +
                        ("childBlocks=" + childBlocks).indent(2)).indent(2) + System.lineSeparator() +
                '}';
    }
}
