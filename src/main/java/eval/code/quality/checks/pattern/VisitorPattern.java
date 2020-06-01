package eval.code.quality.checks.pattern;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.checks.DesignPattern;
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
public class VisitorPattern extends DesignPattern {

    private final String parentName;
    private final List<String> childrenName;
    private final String visitorName;

    public VisitorPattern(ContentProvider contentProvider, String parentName, List<String> childrenName, String visitorName) {
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
        List<MethodDeclaration> visitorMethod = visitor.getMethods().stream()
                .filter(method -> method.getParameters().stream()
                        .map(parameter -> parameter.getType().asString())
                        .anyMatch(childrenSimpleName::contains)).collect(Collectors.toList());
        List<String> visitorMethodsParametersName = visitorMethod.stream()
                .map(method -> method.getParameters().stream()
                        .map(parameter -> parameter.getType().asString())
                        .findFirst().orElseThrow())
                .collect(Collectors.toList());
        evaluator.add(() -> visitorMethodsParametersName.containsAll(childrenSimpleName), "visitor has a visit method for all child");
        BooleanExpression hasAcceptMethodParent = new BooleanSimple(() -> hasAcceptMethodWithVisitor(parent), "Parent interface has accept method to be implemented by children");
        BooleanExpression hasAcceptMethodChildren = new BooleanSimple(() -> children.stream().allMatch(this::hasAcceptMethodWithVisitor), "Every children implements accept method");
        evaluator.add(new BooleanOr(hasAcceptMethodParent, hasAcceptMethodChildren));
        evaluator.add(new BooleanSimple(() -> visitorMethod.stream().allMatch(m -> m.getNameAsString().equals("visit")), "Visit method are called 'visit'", false));
        return evaluator;
    }

    @Override
    public String getName() {
        return "visitor pattern for parent " + addChevrons(parentName) + ", children " + addChevrons(childrenName.toString()) + " and visitor " + addChevrons(visitorName);
    }

    private boolean hasAcceptMethodWithVisitor(ClassOrInterfaceDeclaration classDecl) {
        return classDecl.getMethodsByName("accept").stream().anyMatch(method -> method.getParameterByType(getSimpleName(visitorName)).isPresent());
    }
}
