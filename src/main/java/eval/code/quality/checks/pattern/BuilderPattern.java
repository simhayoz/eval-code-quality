package eval.code.quality.checks.pattern;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ReturnStmt;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.checks.DesignPattern;
import eval.code.quality.utils.evaluator.BooleanEvaluator;
import eval.code.quality.utils.evaluator.BooleanSimple;

import java.util.function.Supplier;

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
public class BuilderPattern extends DesignPattern {

    private final String productName;
    private final String builderName;

    public BuilderPattern(ContentProvider contentProvider, String productName, String builderName) {
        super(contentProvider);
        this.productName = productName;
        this.builderName = builderName;
    }

    @Override
    protected BooleanEvaluator getEvaluator(ContentProvider contentProvider) throws ClassNotFoundException {
        ClassOrInterfaceDeclaration builder = contentProvider.findClassBy(builderName).orElseThrow(() -> new ClassNotFoundException(builderName));
        BooleanEvaluator evaluator = new BooleanEvaluator();
        Supplier<Boolean> hasConstructPart = () -> builder.getMethods().stream().anyMatch(m -> m.hasModifier(Modifier.Keyword.PUBLIC)
                && m.getType().toString().equals(getSimpleName(builderName))
                && m.findAll(ReturnStmt.class).stream().allMatch(r -> r.getExpression().map(Expression::isThisExpr).orElse(false)));
        evaluator.add(new BooleanSimple(hasConstructPart, "builder has at least one method of construction that return this"));
        Supplier<Boolean> hasBuildMethod = () -> builder.getMethods().stream().anyMatch(m -> m.hasModifier(Modifier.Keyword.PUBLIC)
                && m.getType().toString().equals(getSimpleName(productName)));
        evaluator.add(new BooleanSimple(hasBuildMethod, "builder has a method that construct product"));
        evaluator.add(new BooleanSimple(() -> builder.getMethods().stream().filter(m -> m.hasModifier(Modifier.Keyword.PUBLIC)
                && m.getType().toString().equals(getSimpleName(productName))).anyMatch(m -> m.getNameAsString().equals("build") || m.getNameAsString().equals("construct")), "method to construct product is called 'build' or 'construct' for easier reading", false));
        return evaluator;
    }

    @Override
    public String getName() {
        return "builder pattern for product " + addChevrons(productName) + " and builder " + addChevrons(builderName);
    }
}
