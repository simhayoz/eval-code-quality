package eval.code.tests;

import java.util.List;

import eval.code.tools.pos.Range;
import eval.code.tools.pos.Position;

/**
 * Super class of all tests
 * 
 * @author Simon Hayoz
 */
public abstract class Test {

    protected static String NAME;
    protected boolean verbose;

    /**
     * Runs the current test with standard output if needed (verbose)
     * 
     * @param verbose whether to print error or not
     * @return a list of Position of all found error
     */
    public List<Position> runTest(boolean verbose) {
        this.verbose = verbose;
        if (verbose)
            System.out.println("------------- Starting test: " + NAME + " -------------");
        List<Position> result = test();
        if (verbose)
            System.out.println("-------------   End test: " + NAME + "    -------------\n");
        return result;
    }

    /**
     * Get the result of the test
     * 
     * @return a list of Position of all found error
     */
    protected abstract List<Position> test();

    protected void printLine(String l) {
        if (verbose)
            System.out.println(" > " + l);
    }

    protected void printError(String l) {
        if (verbose)
            System.out.println(" > " + l);
    }

    protected void printError(Position p, int was, int expected) {
        printError(p, Integer.toString(was), Integer.toString(expected));
    }

    protected void printError(Position p, String was, int expected) {
        printError(p, was, Integer.toString(expected));
    }

    protected void printError(Position p, int was, String expected) {
        printError(p, Integer.toString(was), expected);
    }

    protected void printError(Position p, String was, String expected) {
        printError(((p instanceof Range) ? "Block at " : "Line ") + p + " is not correctly indented (was " + was
                + ", expected " + expected + ")");
    }

    protected void printSuccess() {
        if (verbose)
            System.out.println("Test " + NAME + ": Success");
    }
}