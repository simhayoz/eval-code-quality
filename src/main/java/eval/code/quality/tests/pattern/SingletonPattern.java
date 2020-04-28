package eval.code.quality.tests.pattern;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import eval.code.quality.utils.Matcher;
import eval.code.quality.utils.booleanExpr.BooleanExpr;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static eval.code.quality.utils.booleanExpr.BooleanExpr.*;

public class SingletonPattern extends Matcher<ClassOrInterfaceDeclaration> {
    private BooleanExpr booleanExpr;
    @Override
    public boolean matches(ClassOrInterfaceDeclaration actual) {
        List<ConstructorDeclaration> constructor = actual.getConstructors();
        List<MethodDeclaration> methods = actual.getMethods().stream().filter(m -> m.hasModifier(Modifier.Keyword.STATIC) && m.hasModifier(Modifier.Keyword.PUBLIC) && m.getType().toString().equals(actual.getNameAsString())).collect(Collectors.toList());
        List<FieldDeclaration> variables = actual.getFields().stream().filter(v -> v.hasModifier(Modifier.Keyword.STATIC) && v.getVariables().stream().anyMatch(e -> e.getType().toString().equals(actual.getNameAsString()))).collect(Collectors.toList());
        BooleanExpr hasPrivateConstructor = expr(() -> !actual.isInterface(), "class is not an interface")
                .and(expr(() -> constructor.size() == 1 && constructor.get(0).hasModifier(Modifier.Keyword.PRIVATE), "constructor is unique and private"));
        BooleanExpr hasStaticVariable = expr(() -> variables.size() == 1, "there exists a unique static variable of type: " + actual.getNameAsString());
        Supplier<Boolean> staticInit = () -> variables.get(0).hasModifier(Modifier.Keyword.FINAL)
                && variables.get(0).getVariables().get(0).getInitializer().isPresent();
        Supplier<Boolean> lazyInit = () -> checkOnUnique(methods.get(0).findAll(AssignExpr.class).stream().filter(a -> a.getTarget().isNameExpr() && a.getTarget().asNameExpr().getName().equals(variables.get(0).getVariables().get(0).getName())).collect(Collectors.toList()),
                (assignExpr) -> assignExpr.findAncestor(IfStmt.class).map(ifStmt -> ifStmtCondition(variables.get(0).getVariables().get(0).getName(), ifStmt)).orElse(false));
        Supplier<Boolean> publicStaticMethod = () -> checkOnUnique(methods, method -> checkOnUnique(method.findAll(ReturnStmt.class), returnStmt -> returnStmt.getExpression().map(expression -> expression.isNameExpr()
                && expression.asNameExpr().getName().equals(variables.get(0).getVariables().get(0).getName())).orElse(false)));
        Supplier<Boolean> publicInstanceVariable = () -> variables.get(0).hasModifier(Modifier.Keyword.PUBLIC)
                && staticInit.get();
        BooleanExpr canAccessInstance = expr(publicInstanceVariable, "static instance variable is publicly accessible and statically initialized")
                .or(expr(publicStaticMethod, "static instance variable is accessible through a public static method").and(expr(staticInit, "instance variable is statically initialized")
                        .or(expr(lazyInit, "instance getter method only lazy init instance once")), "method accessible instance"));
        booleanExpr = hasPrivateConstructor.and(hasStaticVariable.and(canAccessInstance));
        return booleanExpr.evaluate();
    }

    @Override
    public void describeMismatch(ClassOrInterfaceDeclaration actual) {
        System.out.println("Singleton Pattern for the class " + actual.getNameAsString() + ": expected: " + System.lineSeparator() + booleanExpr.describeMismatch().indent(2) + " but was false");
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
