package eval.code.quality.tests;

import eval.code.quality.utils.description.Description;
import eval.code.quality.utils.description.DescriptionBuilder;
import eval.code.quality.utils.reporter.InferMapProperty;

/**
 * Base class for tests.
 */
public abstract class Test {
    private Report report;
    private boolean verbose;
    protected InferMapProperty inferMapProperty = new InferMapProperty(this);

    /**
     * Run the current test and get the report of any errors and warnings found.
     *
     * @return the {@code Report} of the current test
     */
    public Report run() {
        return run(false);
    }

    /**
     * Runs the current test with standard output if needed (verbose).
     *
     * @param verbose whether to print error or not
     */
    public Report run(boolean verbose) {
        report = new Report();
        this.verbose = verbose;
        printLine("------------- Starting test: " + getName() + " -------------");
        test();
        printLine("-------------   End test: " + getName() + "    -------------" + System.lineSeparator());
        return report;
    }

    public void addError(DescriptionBuilder builder) {
        Description error = builder.build();
        printDebugError(error);
        report.addError(error);
    }

    public void addError(Description error) {
        printDebugError(error);
        report.addError(error);
    }

    public void addWarning(DescriptionBuilder builder) {
        Description warning = builder.build();
        printDebugWarning(warning);
        report.addWarning(warning);
    }

    public void addWarning(Description warning) {
        printDebugWarning(warning);
        report.addWarning(warning);
    }

    /**
     * Run the current test.
     */
    protected abstract void test();

    /**
     * Get the name of the current test.
     *
     * @return the name of the current test
     */
    protected abstract String getName();

    private void printLine(String s) {
        if (verbose)
            System.out.println(s);
    }

    private void printDebugError(Description e) {
        printLine(" > (" + getName() + ") error:" + e.toString());
    }

    private void printDebugWarning(Description e) {
        printLine(" > (" + getName() + ") warning:" + e.toString());
    }

    @Override
    public String toString() {
        return "Result for " + getName() + ":" + System.lineSeparator() + report.toString().indent(1);
    }
}
