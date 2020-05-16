package eval.code.quality.utils.evaluator;

import eval.code.quality.utils.Preconditions;

import java.util.function.Supplier;

/**
 * Represents a boolean expression with an error description.
 */
public class BooleanSimple extends BooleanExpression {
    private final Supplier<Boolean> value;
    private final String errorMessage;
    private final boolean isError;

    /**
     * Create a new {@code BooleanSimple} expression.
     *
     * @param value        the supplier for the boolean value
     * @param errorMessage the error message
     */
    public BooleanSimple(Supplier<Boolean> value, String errorMessage) {
        this(value, errorMessage, true);
    }

    /**
     * Create a new {@code BooleanSimple} expression.
     *
     * @param value        the supplier for the boolean value
     * @param errorMessage the error message
     * @param isError      whether it is an error or a warning
     */
    public BooleanSimple(Supplier<Boolean> value, String errorMessage, boolean isError) {
        Preconditions.checkArg(value != null, "Boolean value is null");
        Preconditions.checkArg(errorMessage != null, "Error description is null");
        this.value = value;
        this.errorMessage = errorMessage;
        this.isError = isError;
    }

    @Override
    public boolean evaluate() {
        return value.get();
    }

    @Override
    public String describeMismatch() {
        return "<" + errorMessage + ">";
    }

    @Override
    public boolean isError() {
        return isError;
    }
}
