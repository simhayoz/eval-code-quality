package eval.code.quality.block;

import com.github.javaparser.Position;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import eval.code.quality.position.SinglePosition;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class ParentBlockTest {

    @Test void canCreateAndGetInfoFromAnnotationBlock() {
        String content = "@interface ClassPreamble {\n" +
                "   String author();\n" +
                "   String date();\n" +
                "   int currentRevision() default 1;\n" +
                "   String lastModified() default \"N/A\";\n" +
                "   String lastModifiedBy() default \"N/A\";\n" +
                "   // Note use of array\n" +
                "   String[] reviewers();\n" +
                "}";
        checkWithBlock(content, AnnotationDeclaration.class, AnnotationBlock::new, Position.pos(1, 1), new SinglePosition(1, 26), new SinglePosition(9, 1), 1);
    }

    @Test void canCreateAndGetInfoFromSimpleBlock() {
        String content = "class ForLoopExample {\n" +
                "    public static void main(String args[]){\n" +
                "         for(int i=10; i>1; i--){\n" +
                "              System.out.println(\"The value of i is: \"+i);\n" +
                "         }\n" +
                "    }\n" +
                "}";
        checkWithBlock(content, ForStmt.class,
                forStmt -> new Block(forStmt, forStmt.getBody().asBlockStmt()),
                Position.pos(3, 10),
                new SinglePosition(3, 33),
                new SinglePosition(5, 10),
                3);
    }

    @Test void canCreateCallable() {
        String content = "class ForLoopExample {\n" +
                "    public static void main(String args[]) {\n" +
                "         for(int i=10; i>1; i--){\n" +
                "              System.out.println(\"The value of i is: \"+i);\n" +
                "         }\n" +
                "    }\n" +
                "}";
        checkWithBlock(content, MethodDeclaration.class,
                methodDeclaration -> new CallableBlock(methodDeclaration, methodDeclaration.getBody().get().asBlockStmt(), content),
                Position.pos(2, 5),
                new SinglePosition(2, 44),
                new SinglePosition(6, 5),
                2);

    }

    @Test void canCreateAndGetInfoFromSimpleLambdaBlock() {
        String content = "class Test {\n" +
                "    @Override\n" +
                "    public static Function<String, Integer> test() {\n" +
                "         return sContent -> {\n" +
                "              System.out.println(\"output \" + sContent);\n" +
                "              return sContent.length();\n" +
                "         };\n" +
                "    }\n" +
                "}";
        checkWithBlock(content, LambdaExpr.class,
                lambdaExpr -> new LambdaBlock(lambdaExpr, content),
                Position.pos(4, 10),
                new SinglePosition(4, 29),
                new SinglePosition(7, 10),
                4);
    }

    @Test void canCreateAndGetInfoFromSimpleClassOrInterface() {
        String content = "class Test {\n" +
                "}";
        checkWithBlock(content, ClassOrInterfaceDeclaration.class, ClassOrInterfaceBlock::new, Position.pos(1, 1), new SinglePosition(1, 12), new SinglePosition(2, 1), 1);
    }

    private <T extends Node> void checkWithBlock(String content, Class<T> clazz, Function<T, ParentBlock> func, Position parentStart, SinglePosition bracesStart, SinglePosition bracesEnd, int parentLineEnd) {
        checkWithBlock(content, clazz, func, true, parentStart, bracesStart, bracesEnd, parentLineEnd);
    }

    private <T extends Node> void checkWithBlock(String content, Class<T> clazz, Function<T, ParentBlock> func, boolean hasBraces, Position parentStart, SinglePosition bracesStart, SinglePosition bracesEnd, int parentLineEnd) {
        T element = StaticJavaParser.parse(content).findFirst(clazz).get();
        ParentBlock parentBlock = func.apply(element);
        if(hasBraces) {
            assertThat(parentBlock.bracesPosition.begin, is(bracesStart));
            assertThat(parentBlock.bracesPosition.end, is(bracesEnd));
        } else {
            assertThat(parentBlock.bracesPosition, nullValue());
        }

        assertThat(parentBlock.getParentStart(), is(parentStart));
        assertThat(parentBlock.getParentLineEnd(), is(parentLineEnd));
    }
}
