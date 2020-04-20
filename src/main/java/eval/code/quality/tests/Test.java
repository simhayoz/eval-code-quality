package eval.code.quality.tests;

import eval.code.quality.position.MultiplePosition;
import eval.code.quality.position.Position;
import eval.code.quality.utils.Error;
import eval.code.quality.utils.MultiplePossibility;
import eval.code.quality.utils.ReportPosition;

import java.util.*;
import java.util.function.Function;
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

    public <T> void reportWith(Map<T, List<Position>> map, Function<T, String> func, String type) {
        reportWith(map, func, func, type, true);
    }

    public <T> void reportWith(Map<T, List<Position>> map, Function<T, String> wasFor, Function<T, String> notExpected, String type, boolean shouldReportWrongWhenMultiplePossibility) {
        if(map.size() > 1) {
            List<T> goodList = new ArrayList<>();
            List<T> wrongList = new ArrayList<>();
            setGoodAndWrongList(map, goodList, wrongList);
            if(goodList.size() > 1) {
                reportIfMultiplePossibility(map, goodList, wasFor, type);
            }
            reportNotExpected(map, goodList, wrongList, wasFor, notExpected);
        }
    }

    public <T> void reportIfMultiplePossibility(Map<T, List<Position>> map, List<T> goodList, Function<T, String> wasFor, String type) {
        Map<Position, String> intended = new HashMap<>();
        goodList.forEach(i -> intended.put(
                (map.get(i).size() > 1 ? new MultiplePosition(map.get(i)) : map.get(i).get(0)), wasFor.apply(i)));
        addError(MultiplePossibility.at(intended, "Multiple possible properties for " + type + ", should be all the same"));
    }

    public <T> void reportNotExpected(Map<T, List<Position>> map, List<T> goodList, List<T> wrongList, Function<T, String> expectedFor, Function<T, String> notExpected) {
        String expected = goodList.size() == 1 ? expectedFor.apply(goodList.get(0)) : goodList.stream().map(expectedFor).collect(Collectors.toList()).toString();
        for (T element : wrongList) {
            if (map.get(element).size() > 1) {
                MultiplePosition positions = new MultiplePosition();
                map.get(element).forEach(positions::add);
                addError(ReportPosition.at(positions, expected, notExpected.apply(element)));
            } else {
                addError(ReportPosition.at(map.get(element).get(0), expected, notExpected.apply(element)));
            }
        }
    }

    public <T> void setGoodAndWrongList(Map<T, List<Position>> map, List<T> goodList, List<T> wrongList) {
        Map<T, Integer> indentationCount = map.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
        int maxNumberOfElement = Collections.max(indentationCount.values());
        indentationCount.forEach((key, value) -> (value == maxNumberOfElement ? goodList : wrongList).add(key));
    }

    @Override
    public String toString() {
        return "Result for " + getName() + ":" + System.lineSeparator() + report.toString().indent(1);
    }
}
