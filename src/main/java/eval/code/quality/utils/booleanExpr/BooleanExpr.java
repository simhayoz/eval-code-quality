package eval.code.quality.utils.booleanExpr;

import eval.code.quality.tests.Report;

import java.util.function.Supplier;

public abstract class BooleanExpr {

    public abstract boolean evaluate();
    public abstract String describeMismatch();

    public BooleanExpr and(BooleanExpr e2) {
        return new AndExpr(this, e2);
    }

    public BooleanExpr and(BooleanExpr e2, String description) {
        return new AndExpr(this, e2, description);
    }

    public BooleanExpr or(BooleanExpr e2) {
        return new OrExpr(this, e2);
    }

    public static BooleanExpr expr(Supplier<Boolean> value, String errorMessage) {
        return new NamedBoolean(value, errorMessage);
    }

    public static BooleanExpr not(BooleanExpr e) {
        return new NotExpr(e);
    }

    public static BooleanExpr and(BooleanExpr e1, BooleanExpr e2) {
        return new AndExpr(e1, e2);
    }

    public static BooleanExpr or(BooleanExpr e1, BooleanExpr e2) {
        return new OrExpr(e1, e2);
    }
}
