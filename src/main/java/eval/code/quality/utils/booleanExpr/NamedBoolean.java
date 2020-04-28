package eval.code.quality.utils.booleanExpr;

import java.util.function.Supplier;

public class NamedBoolean extends BooleanExpr {
    public final Supplier<Boolean> value;
    public final String errorMessage;

    public NamedBoolean(Supplier<Boolean> value, String errorMessage) {
        this.value = value;
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean evaluate() {
        return value.get();
    }

    @Override
    public String describeMismatch() {
        return "<" + errorMessage + ">";
    }
}
