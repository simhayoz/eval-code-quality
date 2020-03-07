package eval.code.tests;

import eval.code.tools.pos.ReportPosition;
import eval.code.tools.pos.Position;

/**
 * Super class of all tests
 * 
 * @author Simon Hayoz
 */
public abstract class Test {

    protected static String NAME;
    protected boolean verbose;

    private final Report report = new Report();

    public Report runTest() {
        return runTest(false);
    }

    /**
     * Runs the current test with standard output if needed (verbose)
     * 
     * @param verbose whether to print error or not
     */
    public Report runTest(boolean verbose) {
        report.clear();
        this.verbose = verbose;
        printLine("------------- Starting test: " + NAME + " -------------");
        test();
        printLine("-------------   End test: " + NAME + "    -------------\n");
        return report;
    }

    public void addError(Position position, String report) {
        addError(ReportPosition.at(position, report));
    }

    public void addError(Position position, String expected, String was) {
        addError(ReportPosition.at(position, expected, was));
    }

    public void addError(Position position, int expected, int was) {
        addError(ReportPosition.at(position, ""+expected, ""+was));
    }

    public void addError(Position position, String expected, int was) {
        addError(ReportPosition.at(position, expected, ""+was));
    }

    public void addError(Position position, int expected, String was) {
        addError(ReportPosition.at(position, ""+expected, was));
    }

    public void addError(ReportPosition error) {
        printError(error);
        report.addError(error);
    }

    public void addWarning(Position position, String report) {
        addWarning(ReportPosition.at(position, report));
    }

    public void addWarning(Position position, String expected, String was) {
        addWarning(ReportPosition.at(position, expected, was));
    }

    public void addWarning(Position position, int expected, int was) {
        addWarning(ReportPosition.at(position, ""+expected, ""+was));
    }

    public void addWarning(Position position, String expected, int was) {
        addWarning(ReportPosition.at(position, expected, ""+was));
    }

    public void addWarning(Position position, int expected, String was) {
        addWarning(ReportPosition.at(position, ""+expected, was));
    }

    public void addWarning(ReportPosition warning) {
        printWarning(warning);
        report.addWarning(warning);
    }

    /**
     * Get the result of the test, add every errors and warnings to the report
     */
    protected abstract void test();

    private void printError(ReportPosition e) {
        if (verbose)
            System.out.println(" > (" + NAME + ") " + e.toString());
    }

    private void printWarning(ReportPosition e) {
        if (verbose)
            System.out.println(" > (" + NAME + ") " + e.toString());
    }

    private void printLine(String s) {
        if (verbose)
            System.out.println(s);
    }

    @Override
    public String toString() {
        return "Result for " + NAME + ":\n" + report.toString();
    }
}