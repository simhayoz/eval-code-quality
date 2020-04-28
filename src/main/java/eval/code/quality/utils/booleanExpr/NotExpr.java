package eval.code.quality.utils.booleanExpr;

public class NotExpr extends BooleanExpr {

    private final BooleanExpr b;

    public NotExpr(BooleanExpr b) {
        this.b = b;
    }

    @Override
    public boolean evaluate() {
        return !b.evaluate();
    }

    @Override
    public String describeMismatch() {
        return "not(" + b.describeMismatch() + ")";
    }
}
