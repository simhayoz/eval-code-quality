package eval.code.quality.utils.evaluator;

/**
 * Represents a boolean expression for easier error message on failing expression.
 */
public abstract class BooleanExpression {

    /**
     * Evaluate the expression.
     *
     * @return the result of the evaluation
     */
    public abstract boolean evaluate();

    /**
     * Describe the mismatch that occurred.
     *
     * @return the mismatch that occurred
     */
    public abstract String describeMismatch();

    /**
     * Get whether this is an error or a warning.
     *
     * @return whether this is an error or a warning
     */
    public abstract boolean isError();
}
