package eval.code.quality.utils.evaluator;

public class BooleanOr extends BooleanExpression {

    private final BooleanExpression b1;
    private final BooleanExpression b2;

    public BooleanOr(BooleanExpression b1, BooleanExpression b2) {
        this.b1 = b1;
        this.b2 = b2;
    }

    @Override
    public boolean evaluate() {
        return b1.evaluate() || b2.evaluate();
    }

    @Override
    public String describeMismatch() {
        return "("+b1.describeMismatch() + " or " + System.lineSeparator() + b2.describeMismatch()+")";
    }

    @Override
    public boolean isError() {
        return b1.isError() || b2.isError();
    }
}
