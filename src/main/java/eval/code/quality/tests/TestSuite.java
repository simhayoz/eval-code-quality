package eval.code.quality.tests;

import eval.code.quality.utils.Preconditions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a test suite from multiple tests.
 */
public class TestSuite {
    private final List<Test> tests;
    private final Map<String, Report> testResults;

    public TestSuite(List<Test> tests) {
        Preconditions.checkArg(tests != null, "The test suite can not be null");
        this.tests = tests;
        this.testResults = new HashMap<>();
    }

    /**
     * Run all tests in the test suite.
     * @return the map of test -> report
     */
    public Map<String, Report> runTests() {
        return runTests(false);
    }

    /**
     * Run all tests with or without verbose.
     * @param verbose whether the test should print debug verbose or not
     * @return the map of test -> report
     */
    public Map<String, Report> runTests(boolean verbose) {
        testResults.clear();
        for(Test test: tests) {
            testResults.put(test.getName(), test.run(verbose));
        }
        return testResults;
    }

    private String mapToString() {
        return testResults.entrySet().stream().map(e -> "Test for " + e.getKey() + ": " + System.lineSeparator() + e.getValue().toString().indent(1)).collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public String toString() {
        return "TestSuite: " + System.lineSeparator() + mapToString().indent(1);
    }
}
