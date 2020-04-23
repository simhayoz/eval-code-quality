package eval.code.quality.tests;

import eval.code.quality.position.Position;
import eval.code.quality.utils.*;
import eval.code.quality.utils.Error;

import java.util.*;
import java.util.stream.Collectors;

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

    public void addError(Error error) {
        printError(error);
        report.addError(error);
    }

    public void addWarning(Error warning) {
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

    public <T> void checkAndReport(Map<T, List<Position>> map, boolean shouldReport) {
        checkAndReport(map, new ExpectedReporter<>(), new NotExpectedReporter<>(), shouldReport);
    }

    public <T> void checkAndReport(Map<T, List<Position>> map, String name, boolean shouldReport) {
        checkAndReport(map, new NamedExpectedReporter<>(name), new NamedNotExpectedReporter<>(name), shouldReport);
    }

    public <T> void checkAndReport(Map<T, List<Position>> map, ExpectedReporter<T> expectedReporter, NotExpectedReporter<T> notExpectedReporter, boolean shouldReport) {
        if(map.size() > 1) {
            List<T> goodList = new ArrayList<>();
            List<T> wrongList = new ArrayList<>();
            setGoodAndWrongList(map, goodList, wrongList);
            if(goodList.size() > 1) {
                addIfNotNull(expectedReporter.reportMultipleExpected(map, goodList));
                if(shouldReport) {
                    notExpectedReporter.reportNotExpected(map, goodList, wrongList).forEach(this::addError);
                }
            } else {
                expectedReporter.doOnUniqueExpected(map, goodList.get(0));
                notExpectedReporter.reportNotExpected(map, goodList, wrongList).forEach(this::addError);
                notExpectedReporter.doOnNotExpected(map, goodList, wrongList);
            }
        }
    }

    public <T> void setGoodAndWrongList(Map<T, List<Position>> map, List<T> goodList, List<T> wrongList) {
        Map<T, Integer> indentationCount = map.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
        int maxNumberOfElement = Collections.max(indentationCount.values());
        indentationCount.forEach((key, value) -> (value == maxNumberOfElement ? goodList : wrongList).add(key));
    }

    private void addIfNotNull(Error error) {
        if(error != null) {
            addError(error);
        }
    }

    @Override
    public String toString() {
        return "Result for " + getName() + ":" + System.lineSeparator() + report.toString().indent(1);
    }
}
