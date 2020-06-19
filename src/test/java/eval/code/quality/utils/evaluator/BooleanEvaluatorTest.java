package eval.code.quality.utils.evaluator;

import eval.code.quality.checks.Check;
import eval.code.quality.checks.Report;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

public class BooleanEvaluatorTest {

    @Test void nullInputThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new BooleanEvaluator(null));
    }

    @Test void canCreateSimpleExpression() {
        BooleanEvaluator booleanEvaluator = new BooleanEvaluator();
        booleanEvaluator.add(() -> true, "error0");
        booleanEvaluator.add(() -> false, "error1", false);
        booleanEvaluator.add(new BooleanSimple(() -> false, "error2"));
        booleanEvaluator.add(() -> true, "error3");
        assertFalse(booleanEvaluator.evaluate());
        Check check = new Check() {
            @Override
            protected void check() {
                booleanEvaluator.reportMismatches(this);
            }

            @Override
            public String getName() {
                return null;
            }
        };
        Report r = check.run();
        assertThat(r.getWarnings(), hasSize(1));
        assertTrue(r.getWarnings().get(0).getDescriptor().getDescription().isPresent());
        assertThat(r.getWarnings().get(0).getDescriptor().getDescription().get(), is("<error1>"));
        assertThat(r.getErrors(), hasSize(1));
        assertTrue(r.getErrors().get(0).getDescriptor().getDescription().isPresent());
        assertThat(r.getErrors().get(0).getDescriptor().getDescription().get(), is("<error2>"));
    }
}
