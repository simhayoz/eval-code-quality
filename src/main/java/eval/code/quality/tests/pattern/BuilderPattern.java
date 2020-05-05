package eval.code.quality.tests.pattern;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ReturnStmt;
import eval.code.quality.utils.Matcher;
import eval.code.quality.utils.Tuple;
import eval.code.quality.utils.booleanExpr.BooleanExpr;

import static eval.code.quality.utils.booleanExpr.BooleanExpr.expr;

public class BuilderPattern extends Matcher<Tuple<ClassOrInterfaceDeclaration, ClassOrInterfaceDeclaration>> {
    private BooleanExpr booleanExpr;
    @Override
    public boolean matches(Tuple<ClassOrInterfaceDeclaration, ClassOrInterfaceDeclaration> actual) {
        BooleanExpr hasConstructPart = expr(() -> actual._2.getMethods().stream().anyMatch(m -> m.hasModifier(Modifier.Keyword.PUBLIC)
                && m.getType().toString().equals(actual._1.getNameAsString())
                && m.findAll(ReturnStmt.class).stream().allMatch(r -> r.getExpression().map(Expression::isThisExpr).orElse(false))), "builder has at least a method of construction that return this");
        BooleanExpr hasBuildMethod = expr(() -> actual._2.getMethods().stream().anyMatch(m -> m.hasModifier(Modifier.Keyword.PUBLIC)
                && m.getType().toString().equals(actual._1.getNameAsString())), "builder has a method that construct product");
        booleanExpr = hasBuildMethod.and(hasConstructPart);
        return booleanExpr.evaluate();
    }

    @Override
    public void describeMismatch(Tuple<ClassOrInterfaceDeclaration, ClassOrInterfaceDeclaration> actual) {
        System.out.println("Builder Pattern for the product " + actual._1.getNameAsString() + " and builder " + actual._2.getNameAsString() + ": expected: " + System.lineSeparator() + booleanExpr.describeMismatch().indent(2) + " but was false");
    }
}
