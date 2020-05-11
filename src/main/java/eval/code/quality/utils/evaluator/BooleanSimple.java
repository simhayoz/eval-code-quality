package eval.code.quality.utils.evaluator;

import java.util.function.Supplier;

public class BooleanSimple extends BooleanExpression {
    private final Supplier<Boolean> value;
    private final String errorMessage;
    private final boolean isError;

    public BooleanSimple(Supplier<Boolean> value, String errorMessage) {
        this(value, errorMessage, true);
    }

    public BooleanSimple(Supplier<Boolean> value, String errorMessage, boolean isError) {
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
