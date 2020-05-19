package eval.code.quality.utils.evaluator;

import eval.code.quality.tests.Report;
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
         eval.code.quality.tests.Test test = new eval.code.quality.tests.Test() {
             @Override
             protected void test() {
                 booleanEvaluator.reportMismatches(this);
             }

             @Override
             protected String getName() {
                 return null;
             }
         };
         Report r = test.run();
         assertThat(r.getWarnings(), hasSize(1));
         assertTrue(r.getWarnings().get(0).getDescription().isPresent());
         assertThat(r.getWarnings().get(0).getDescription().get(), is("<error1>"));
         assertThat(r.getErrors(), hasSize(1));
         assertTrue(r.getErrors().get(0).getDescription().isPresent());
         assertThat(r.getErrors().get(0).getDescription().get(), is("<error2>"));
     }
}
