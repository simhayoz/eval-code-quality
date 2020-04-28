package eval.code.quality.utils.booleanExpr;

public class AndExpr extends BooleanExpr {

    private final BooleanExpr b1;
    private final BooleanExpr b2;
    private final String description;

    public AndExpr(BooleanExpr b1, BooleanExpr b2) {
        this(b1, b2, "");
    }

    public AndExpr(BooleanExpr b1, BooleanExpr b2, String description) {
        this.b1 = b1;
        this.b2 = b2;
        this.description = description;
    }

    @Override
    public boolean evaluate() {
        if(!b1.evaluate()) {
            return false;
        }
        return b2.evaluate();
    }

    @Override
    public String describeMismatch() {
        String descr = description.isEmpty() ? "" : description + ": ";
        if(!b1.evaluate()) {
            return descr + b1.describeMismatch();
        } else {
            return descr + b2.describeMismatch();
        }
    }
}
