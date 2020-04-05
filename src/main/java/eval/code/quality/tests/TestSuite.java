package eval.code.quality.tests;

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
        this.tests = tests;
        this.testResults = new HashMap<>();
    }

    public Map<String, Report> runTests() {
        return runTests(false);
    }

    public Map<String, Report> runTests(boolean verbose) {
        testResults.clear();
        for(Test test: tests) {
            testResults.put(test.getName(), test.run());
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
