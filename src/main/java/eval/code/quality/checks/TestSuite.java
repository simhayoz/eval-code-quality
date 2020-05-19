package eval.code.quality.checks;

import eval.code.quality.utils.Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a test suite from multiple checks.
 */
public class TestSuite {
    private final List<Check> checks;
    private final Map<String, Report> checkResults;

    /**
     * Create a new empty test suite.
     */
    public TestSuite() {
        this(new ArrayList<>());
    }

    /**
     * Create a new test suite from a list of checks.
     *
     * @param checks the list of test
     */
    public TestSuite(List<Check> checks) {
        Preconditions.checkArg(checks != null, "The test suite can not be null");
        this.checks = checks;
        this.checkResults = new HashMap<>();
    }

    /**
     * Add a check to the test suite.
     *
     * @param check the check to add
     * @return whether the check was added or not
     */
    public boolean add(Check check) {
        return checks.add(check);
    }

    /**
     * Run all checks in the test suite.
     *
     * @return the map of check name -> report
     */
    public Map<String, Report> runChecks() {
        return runChecks(false);
    }

    /**
     * Run all checks with or without verbose.
     *
     * @param verbose whether the checks should print debug verbose or not
     * @return the map of check name -> report
     */
    public Map<String, Report> runChecks(boolean verbose) {
        checkResults.clear();
        for (Check check : checks) {
            checkResults.put(check.getName(), check.run(verbose));
        }
        return checkResults;
    }

    private String mapToString() {
        return checkResults.entrySet().stream().map(e -> "Test for " + e.getKey() + ": " + System.lineSeparator() + e.getValue().toString().indent(1)).collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public String toString() {
        return "TestSuite: " + System.lineSeparator() + mapToString().indent(1);
    }
}
