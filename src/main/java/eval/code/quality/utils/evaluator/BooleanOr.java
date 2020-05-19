package eval.code.quality.utils.evaluator;

import eval.code.quality.utils.Preconditions;

/**
 * Represents a logical "or" between two {@code BooleanExpression}.
 */
public class BooleanOr extends BooleanExpression {

    private final BooleanExpression b1;
    private final BooleanExpression b2;

    /**
     * Create a new {@code BooleanOr} expression.
     *
     * @param b1 the first expression
     * @param b2 the second expression
     */
    public BooleanOr(BooleanExpression b1, BooleanExpression b2) {
        Preconditions.checkArg(b1 != null, "Left part of the boolean expression is null");
        Preconditions.checkArg(b2 != null, "Right part of the boolean expression is null");
        this.b1 = b1;
        this.b2 = b2;
    }

    @Override
    public boolean evaluate() {
        return b1.evaluate() || b2.evaluate();
    }

    @Override
    public String describeMismatch() {
        return "(" + b1.describeMismatch() + " or " + System.lineSeparator() + b2.describeMismatch() + ")";
    }

    @Override
    public boolean isError() {
        return b1.isError() || b2.isError();
    }
}
