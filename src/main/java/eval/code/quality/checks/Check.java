package eval.code.quality.checks;

import eval.code.quality.utils.description.Description;
import eval.code.quality.utils.description.DescriptionBuilder;
import eval.code.quality.utils.reporter.InferMapProperty;

/**
 * Base class for checks.
 */
public abstract class Check {
    private Report report;
    private boolean verbose;
    protected InferMapProperty inferMapProperty = new InferMapProperty(this);

    /**
     * Run the current check and get the report of any errors and warnings found.
     *
     * @return the {@code Report} of the current check
     */
    public Report run() {
        return run(false);
    }

    /**
     * Runs the current check with standard output if needed (verbose).
     *
     * @param verbose whether to print error or not
     */
    public Report run(boolean verbose) {
        report = new Report();
        this.verbose = verbose;
        printLine("------------- Starting check: " + getName() + " -------------");
        check();
        printLine("-------------   End check: " + getName() + "    -------------" + System.lineSeparator());
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
     * Run the current check.
     */
    protected abstract void check();

    /**
     * Get the name of the current check.
     *
     * @return the name of the current check
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