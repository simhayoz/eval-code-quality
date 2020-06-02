package eval.code.quality.utils.evaluator;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

public class BooleanExpressionTest {

    @Test void nullThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new BooleanSimple(null, "error"));
        assertThrows(IllegalArgumentException.class, () -> new BooleanSimple(() -> true, null));
        assertThrows(IllegalArgumentException.class, () ->
                new BooleanOr(new BooleanSimple(() -> true, "error"), null));
        assertThrows(IllegalArgumentException.class, () ->
                new BooleanOr(null, new BooleanSimple(() -> true, "error")));
        assertThrows(IllegalArgumentException.class, () ->
                new BooleanAnd(new BooleanSimple(() -> true, "error"), null));
        assertThrows(IllegalArgumentException.class, () ->
                new BooleanAnd(null, new BooleanSimple(() -> true, "error")));
    }

    @Test void canCreateAndEvaluateSimpleExpression() {
        BooleanExpression b1 = new BooleanSimple(() -> true, "error1");
        BooleanExpression b2 = new BooleanSimple(() -> false, "error2", false);
        assertTrue(b1.evaluate());
        assertFalse(b2.evaluate());
        assertThat(b1.describeMismatch(), is("<error1>"));
        assertThat(b2.describeMismatch(), is("<error2>"));
        assertTrue(b1.isError());
        assertFalse(b2.isError());
    }

    @Test void canOrBetweenSimple() {
        BooleanExpression b1 = new BooleanSimple(() -> true, "error1");
        BooleanExpression b2 = new BooleanSimple(() -> false, "error2", false);
        BooleanExpression or = new BooleanOr(b1, b2);
        assertTrue(or.evaluate());
        assertThat(or.describeMismatch(), is("(<error1> or \n<error2>)"));
        assertTrue(or.isError());
        or = new BooleanOr(b2, b1);
        assertTrue(or.evaluate());
        assertThat(or.describeMismatch(), is("(<error2> or \n<error1>)"));
        assertTrue(or.isError());
    }

    @Test void canAndBetweenSimple() {
        BooleanExpression b1 = new BooleanSimple(() -> true, "error1");
        BooleanExpression b2 = new BooleanSimple(() -> false, "error2", false);
        BooleanExpression and = new BooleanAnd(b1, b2);
        assertFalse(and.evaluate());
        assertThat(and.describeMismatch(), is("<error2>"));
        assertTrue(and.isError());
        and = new BooleanAnd(b2, b1);
        assertFalse(and.evaluate());
        assertThat(and.describeMismatch(), is("<error2>"));
        assertTrue(and.isError());
    }
}
