package eval.code.quality.tests.pattern;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ReturnStmt;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.tests.DesignPatternTest;
import eval.code.quality.utils.StringError;
import eval.code.quality.utils.booleanExpr.BooleanExpr;

import static eval.code.quality.utils.booleanExpr.BooleanExpr.expr;

/**
 * Check for a builder design pattern.
 * <p>
 * Check the following properties:
 *     <ul>
 *         <li>Builder has a method "build" that return an object of type Product</li>
 *         <li>Builder has at least one method of construction that will return this (i.e. the Builder)</li>
 *     </ul>
 * </p>
 */
public class BuilderPatternTest extends DesignPatternTest {

    private final String productName;
    private final String builderName;
    private BooleanExpr booleanExpr;

    public BuilderPatternTest(ContentProvider contentProvider, String productName, String builderName) {
        super(contentProvider);
        this.productName = productName;
        this.builderName = builderName;
    }

    @Override
    protected boolean enforce(ContentProvider contentProvider) {
        ClassOrInterfaceDeclaration builder = contentProvider.findClassBy(builderName).get();
        BooleanExpr hasConstructPart = expr(() -> builder.getMethods().stream().anyMatch(m -> m.hasModifier(Modifier.Keyword.PUBLIC)
                && m.getType().toString().equals(getSimpleName(builderName))
                && m.findAll(ReturnStmt.class).stream().allMatch(r -> r.getExpression().map(Expression::isThisExpr).orElse(false))), "builder has at least one method of construction that return this");
        BooleanExpr hasBuildMethod = expr(() -> builder.getMethods().stream().anyMatch(m -> m.hasModifier(Modifier.Keyword.PUBLIC)
                && m.getType().toString().equals(getSimpleName(productName))), "builder has a method that construct product");
        booleanExpr = hasBuildMethod.and(hasConstructPart);
        return booleanExpr.evaluate();
    }

    @Override
    protected void describeMismatch() {
        // TODO this
//        System.out.println("Builder Pattern for the product " + productName + " and builder " + builderName + ": expected: " + System.lineSeparator() + booleanExpr.describeMismatch().indent(2) + " but was false");
        addError(new StringError(booleanExpr.describeMismatch()));
    }

    @Override
    protected String getName() {
        return "builder pattern for product " + addChevrons(productName) + " and builder " + addChevrons(builderName);
    }
}
