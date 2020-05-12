package eval.code.quality.utils.complexity;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.ForStmt;

public class ForCE implements ComplexityEstimator<ForStmt> {

    @Override
    public Complexity getComplexityFor(ForStmt element, MethodDeclaration methodDeclaration) {
        if(element.getUpdate().size() == 1 && element.getUpdate().get(0).isUnaryExpr()) {

        }
        return null;
    }
}
