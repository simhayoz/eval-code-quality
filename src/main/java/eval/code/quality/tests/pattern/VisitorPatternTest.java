package eval.code.quality.tests.pattern;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.tests.DesignPatternTest;
import eval.code.quality.utils.evaluator.BooleanEvaluator;
import eval.code.quality.utils.evaluator.BooleanExpression;
import eval.code.quality.utils.evaluator.BooleanOr;
import eval.code.quality.utils.evaluator.BooleanSimple;

import java.util.List;
import java.util.stream.Collectors;

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

    public VisitorPatternTest(ContentProvider contentProvider, String parentName, List<String> childrenName, String visitorName) {
        super(contentProvider);
        this.parentName = parentName;
        this.childrenName = childrenName;
        this.visitorName = visitorName;
    }

    @Override
    protected BooleanEvaluator getEvaluator(ContentProvider contentProvider) throws ClassNotFoundException {
        ClassOrInterfaceDeclaration parent = contentProvider.findClassBy(parentName).orElseThrow(() -> new ClassNotFoundException(parentName));
        List<ClassOrInterfaceDeclaration> children = childrenName.stream().map(cName -> contentProvider.findClassBy(cName).get()).collect(Collectors.toList());
        ClassOrInterfaceDeclaration visitor = contentProvider.findClassBy(visitorName).orElseThrow(() -> new ClassNotFoundException(visitorName));
        BooleanEvaluator evaluator = new BooleanEvaluator();
        List<String> childrenSimpleName = childrenName.stream().map(this::getSimpleName).collect(Collectors.toList());
        List<String> visitorMethodsParametersName = visitor.getMethods().stream()
                .filter(method -> method.getNameAsString().equals("visit"))
                .map(method -> method.getParameters().stream()
                        .map(parameter -> parameter.getType().asString())
                        .filter(childrenSimpleName::contains)
                        .findFirst().orElseThrow())
                .collect(Collectors.toList());
        evaluator.add(() -> visitorMethodsParametersName.containsAll(childrenSimpleName), "visitor has a method \"visit\" for all child");
        BooleanExpression hasAcceptMethodParent = new BooleanSimple(() -> hasAcceptMethodWithVisitor(parent), "Parent interface has accept method to be implemented by children");
        BooleanExpression hasAcceptMethodChildren = new BooleanSimple(() -> children.stream().allMatch(this::hasAcceptMethodWithVisitor), "Every children implements accept method");
        evaluator.add(new BooleanOr(hasAcceptMethodParent, hasAcceptMethodChildren));
        return evaluator;
    }

    @Override
    protected String getName() {
        return "visitor pattern for parent " + addChevrons(parentName) + ", children " + addChevrons(childrenName.toString()) + " and visitor " + addChevrons(visitorName);
    }

    private boolean hasAcceptMethodWithVisitor(ClassOrInterfaceDeclaration classDecl) {
        return classDecl.getMethodsByName("accept").stream().anyMatch(method -> method.getParameterByType(getSimpleName(visitorName)).isPresent());
    }
}
