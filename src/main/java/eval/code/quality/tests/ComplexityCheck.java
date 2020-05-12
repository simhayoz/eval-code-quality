package eval.code.quality.tests;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.complexity.Complexity;

import java.util.List;

public class ComplexityCheck extends Test {

    private final ContentProvider contentProvider;
    private final MethodDeclaration methodDeclaration;
    private final Complexity complexity;

    public ComplexityCheck(ContentProvider contentProvider, MethodDeclaration methodDeclaration, Complexity complexity) {
        this.contentProvider = contentProvider;
        this.methodDeclaration = methodDeclaration;
        this.complexity = complexity;
    }

    @Override
    protected void test() {

    }

    private List<Complexity> getComplexityForLoop() {

    }

    @Override
    protected String getName() {
        return "complexity in TODO for method TODO";
    }
}
