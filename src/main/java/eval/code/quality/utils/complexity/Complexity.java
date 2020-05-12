package eval.code.quality.utils.complexity;

import com.github.javaparser.ast.expr.Expression;

public class Complexity {
    public enum Complex {
        Constant,
        Linear,
        Logarithmic,
        Quadratic,
        Exponential,
        Unknown
    }

    public final Expression expression;

    public Complexity(Expression expression) {
        this.expression = expression;
    }

}
