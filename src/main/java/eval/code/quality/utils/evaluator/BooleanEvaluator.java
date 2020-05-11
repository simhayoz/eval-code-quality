package eval.code.quality.utils.evaluator;

import eval.code.quality.tests.Test;
import eval.code.quality.utils.StringError;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Evaluator for a list of boolean expression, evaluate them like a series of "and" expression.
 */
public class BooleanEvaluator {

    private final List<BooleanExpression> booleanExpressions;

    /**
     * Create a new empty {@code BooleanEvaluator}.
     */
    public BooleanEvaluator() {
        this(new ArrayList<>());
    }

    /**
     * Create a new {@code BooleanEvaluator}.
     *
     * @param booleanExpressions expressions to put in the evaluator
     */
    public BooleanEvaluator(List<BooleanExpression> booleanExpressions) {
        this.booleanExpressions = booleanExpressions;
    }

    /**
     * Add an expression to the list of evaluated expressions.
     *
     * @param expression the expression to add
     * @return the {@code BooleanEvaluator}
     */
    public BooleanEvaluator add(BooleanExpression expression) {
        booleanExpressions.add(expression);
        return this;
    }

    /**
     * Add an expression to the list of evaluated expressions.
     *
     * @param value        the value as a boolean supplier
     * @param errorMessage the error message
     * @return the {@code BooleanEvaluator}
     */
    public BooleanEvaluator add(Supplier<Boolean> value, String errorMessage) {
        booleanExpressions.add(new BooleanSimple(value, errorMessage));
        return this;
    }

    /**
     * Add an expression to the list of evaluated expressions.
     *
     * @param value        the value as a boolean supplier
     * @param errorMessage the error message
     * @param isError      whether it should report an error or a warning
     * @return the {@code BooleanEvaluator}
     */
    public BooleanEvaluator add(Supplier<Boolean> value, String errorMessage, boolean isError) {
        booleanExpressions.add(new BooleanSimple(value, errorMessage, isError));
        return this;
    }

    /**
     * Evaluate the list of expressions.
     *
     * @return the result of the evaluation
     */
    public boolean evaluate() {
        return booleanExpressions.stream().allMatch(expression -> expression.evaluate() || !expression.isError());
    }

    /**
     * Report mismatches to the {@code Test} class.
     *
     * @param test the class to report error and warning to
     */
    public void reportMismatches(Test test) {
        boolean shouldReportNext = true;
        for (BooleanExpression expression : booleanExpressions) {
            if (shouldReportNext) {
                if (!expression.evaluate() && expression.isError()) {
                    test.addError(new StringError(expression.describeMismatch()));
                    shouldReportNext = false;
                } else if (!expression.evaluate()) {
                    test.addWarning(new StringError(expression.describeMismatch()));
                }
            }

        }
    }
}
