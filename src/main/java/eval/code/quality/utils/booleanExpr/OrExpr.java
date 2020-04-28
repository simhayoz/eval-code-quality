package eval.code.quality.utils.booleanExpr;

public class OrExpr extends BooleanExpr {

    private final BooleanExpr b1;
    private final BooleanExpr b2;

    public OrExpr(BooleanExpr b1, BooleanExpr b2) {
        this.b1 = b1;
        this.b2 = b2;
    }

    @Override
    public boolean evaluate() {
        if(b1.evaluate()) {
            return true;
        }
        return b2.evaluate();
    }

    @Override
    public String describeMismatch() {
        return "("+b1.describeMismatch() + " or " + System.lineSeparator() + b2.describeMismatch()+")";
    }
}