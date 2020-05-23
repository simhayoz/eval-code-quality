package eval.code.quality.checks.pattern;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.checks.DesignPattern;
import eval.code.quality.utils.evaluator.*;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Check for a singleton design pattern.
 * <p>
 *     Check the following properties:
 *     <ul>
 *         <li>There exists a unique constructor that is private</li>
 *         <li>Singleton class has a unique variable static of type of the singleton class</li>
 *         <li>This static variable is either accessible through a method or is public and final</li>
 *         <li>If the variable is accessible through a method and is not final it is initialized through the method and only once,
 *         namely the following check should be done before assigning the variable: <pre>var != null</pre></li>
 *     </ul>
 * </p>
 */
public class SingletonPattern extends DesignPattern {

    private final String className;

    public SingletonPattern(ContentProvider contentProvider, String className) {
        super(contentProvider);
        this.className = className;
    }

    @Override
    protected BooleanEvaluator getEvaluator(ContentProvider contentProvider) throws ClassNotFoundException {
        ClassOrInterfaceDeclaration classCU = contentProvider.findClassBy(className).orElseThrow(() -> new ClassNotFoundException(className));
        BooleanEvaluator evaluator = new BooleanEvaluator();
        List<ConstructorDeclaration> constructor = classCU.getConstructors();
        List<MethodDeclaration> methods = classCU.getMethods().stream().filter(m -> m.hasModifier(Modifier.Keyword.STATIC) && m.hasModifier(Modifier.Keyword.PUBLIC) && m.getType().toString().equals(getSimpleName(className))).collect(Collectors.toList());
        List<FieldDeclaration> variables = classCU.getFields().stream().filter(v -> v.hasModifier(Modifier.Keyword.STATIC) && v.getVariables().stream().anyMatch(e -> e.getType().toString().equals(getSimpleName(className)))).collect(Collectors.toList());
        evaluator.add(() -> !classCU.isInterface(), "class is not an interface");
        evaluator.add(() -> constructor.size() == 1 && constructor.get(0).hasModifier(Modifier.Keyword.PRIVATE), "constructor is unique and private");
        evaluator.add(() -> variables.size() == 1, "there exists a unique static variable of type: " + getSimpleName(className));
        Supplier<Boolean> staticInit = () -> variables.get(0).hasModifier(Modifier.Keyword.FINAL)
                && variables.get(0).getVariables().get(0).getInitializer().isPresent();
        Supplier<Boolean> lazyInit = () -> checkOnUnique(methods.get(0).findAll(AssignExpr.class).stream().filter(a -> a.getTarget().isNameExpr() && a.getTarget().asNameExpr().getName().equals(variables.get(0).getVariables().get(0).getName())).collect(Collectors.toList()),
                (assignExpr) -> assignExpr.findAncestor(IfStmt.class).map(ifStmt -> ifStmtCondition(variables.get(0).getVariables().get(0).getName(), ifStmt)).orElse(false));
        Supplier<Boolean>  publicStaticMethod = () -> checkOnUnique(methods, method -> checkOnUnique(method.findAll(ReturnStmt.class), returnStmt -> returnStmt.getExpression().map(expression -> expression.isNameExpr()
                && expression.asNameExpr().getName().equals(variables.get(0).getVariables().get(0).getName())).orElse(false)));
        Supplier<Boolean>  publicInstanceVariable = () -> variables.get(0).hasModifier(Modifier.Keyword.PUBLIC)
                && staticInit.get();
        Supplier<Long> numberInitialization = () -> classCU.findAll(AssignExpr.class).stream().filter(a -> a.getTarget().isNameExpr() && a.getTarget().asNameExpr().getName().equals(variables.get(0).getVariables().get(0).getName())).count();
        BooleanExpression isInitialized = new BooleanSimple(() -> (staticInit.get() && numberInitialization.get() == 0) || numberInitialization.get() == 1, "Static variable of class should be initialized exactly once");
        evaluator.add(isInitialized);
        evaluator.add(new BooleanOr(new BooleanSimple(publicInstanceVariable, "static instance variable is publicly accessible and statically initialized"),
                new BooleanAnd(new BooleanSimple(publicStaticMethod, "static instance variable is accessible through a public static method"),
                        new BooleanOr(new BooleanSimple(staticInit, "instance variable is statically initialized"), new BooleanSimple(lazyInit, "instance getter method only lazy init instance once")))));
        return evaluator;
    }

    @Override
    public String getName() {
        return "singleton pattern for class " + addChevrons(className);
    }

    private <T> boolean checkOnUnique(List<T> list, Function<T, Boolean> check) {
        if(list.size() == 1) {
            return check.apply(list.get(0));
        }
        return false;
    }

    private boolean ifStmtCondition(SimpleName varName, IfStmt ifStmt) {
        if(!ifStmt.getCondition().isBinaryExpr()) {
            return false;
        }
        Expression leftExpr = ifStmt.getCondition().asBinaryExpr().getLeft();
        Expression rightExpr = ifStmt.getCondition().asBinaryExpr().getRight();
        if(ifStmt.getCondition().asBinaryExpr().getOperator().equals(BinaryExpr.Operator.EQUALS)) {
            if(leftExpr.isNameExpr() && leftExpr.asNameExpr().getName().equals(varName)) {
                return rightExpr.isNullLiteralExpr();
            }
            if(rightExpr.isNameExpr() && rightExpr.asNameExpr().getName().equals(varName)) {
                return leftExpr.isNullLiteralExpr();
            }
        }
        return false;
    }
}
