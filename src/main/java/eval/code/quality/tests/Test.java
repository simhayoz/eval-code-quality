package eval.code.quality.tests;

import eval.code.quality.utils.Error;

/**
 * Base class for tests.
 */
public abstract class Test {
    private final Report report = new Report();
    private boolean verbose;

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
        this.verbose = verbose;
        printLine("------------- Starting test: " + getName() + " -------------");
        test();
        printLine("-------------   End test: " + getName() + "    -------------" + System.lineSeparator());
        return report;
    }

    protected void addError(Error error) {
        printError(error);
        report.addError(error);
    }

    protected void addWarning(Error warning) {
        printWarning(warning);
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

    private void printError(Error e) {
        printLine(" > (" + getName() + ") error:" + e.toString());
    }

    private void printWarning(Error e) {
        printLine(" > (" + getName() + ") warning:" + e.toString());
    }

    @Override
    public String toString() {
        return "Result for " + getName() + ":" + System.lineSeparator() + report.toString().indent(1);
    }
}
