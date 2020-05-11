package eval.code.quality.tests.pattern;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.tests.DesignPatternTest;
import eval.code.quality.utils.StringError;
import eval.code.quality.utils.booleanExpr.BooleanExpr;

import java.util.List;
import java.util.stream.Collectors;

import static eval.code.quality.utils.booleanExpr.BooleanExpr.expr;

/**
 * Check for a visitor design pattern.
 * <p>
 * Check the following properties:
 *     <ul>
 *         <li>Visitor has at least one method "visit" per child (as argument)</li>
 *         <li>Parent has method "accept" or all child have it</li>
 *     </ul>
 * </p>
 */
public class VisitorPatternTest extends DesignPatternTest {

    private final String parentName;
    private final List<String> childrenName;
    private final String visitorName;

    private BooleanExpr booleanExpr;

    public VisitorPatternTest(ContentProvider contentProvider, String parentName, List<String> childrenName, String visitorName) {
        super(contentProvider);
        this.parentName = parentName;
        this.childrenName = childrenName;
        this.visitorName = visitorName;
    }

    @Override
    public boolean enforce(ContentProvider contentProvider) {
        ClassOrInterfaceDeclaration parent = contentProvider.findClassBy(parentName).get();
        List<ClassOrInterfaceDeclaration> children = childrenName.stream().map(cName -> contentProvider.findClassBy(cName).get()).collect(Collectors.toList());
        ClassOrInterfaceDeclaration visitor = contentProvider.findClassBy(visitorName).get();
        List<String> childrenSimpleName = childrenName.stream().map(this::getSimpleName).collect(Collectors.toList());
        List<String> visitorMethodsParametersName = visitor.getMethods().stream()
                .filter(method -> method.getNameAsString().equals("visit"))
                .map(method -> method.getParameters().stream()
                        .map(parameter -> parameter.getType().asString())
                        .filter(childrenSimpleName::contains)
                        .findFirst().orElseThrow())
                .collect(Collectors.toList());
        BooleanExpr visitorHasVisitMethod = expr(() -> visitorMethodsParametersName.containsAll(childrenSimpleName), "visitor has a method \"visit\" for all child");
        BooleanExpr parentHasAcceptMethod = expr(() -> hasAcceptMethodWithVisitor(parent), "Parent interface has accept method to be implemented by children");
        BooleanExpr childrenAllImplementAccept = expr(() -> children.stream().allMatch(this::hasAcceptMethodWithVisitor), "Every children implements accept method");
        booleanExpr = visitorHasVisitMethod.and(parentHasAcceptMethod.or(childrenAllImplementAccept));
        return booleanExpr.evaluate();
    }

    @Override
    public void describeMismatch() {
        // TODO this
        addError(new StringError(booleanExpr.describeMismatch()));
    }

    @Override
    protected String getName() {
        return "visitor pattern for parent " + addChevrons(parentName) + ", children " + addChevrons(childrenName.toString()) + " and visitor " + addChevrons(visitorName);
    }

    private boolean hasAcceptMethodWithVisitor(ClassOrInterfaceDeclaration classDecl) {
        return classDecl.getMethodsByName("accept").stream().anyMatch(method -> method.getParameterByType(getSimpleName(visitorName)).isPresent());
    }
}
