package eval.code.quality.checks;

import eval.code.quality.utils.Preconditions;
import eval.code.quality.utils.XMLParsable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a test suite from multiple checks.
 */
public class TestSuite implements XMLParsable {
    private final List<Check> checks;
    private final Map<String, Report> checkResults;
    private final String name;

    /**
     * Create a new empty test suite.
     */
    public TestSuite(String name) {
        this(new ArrayList<>(), name);
    }

    /**
     * Create a new test suite from a list of checks.
     *
     * @param checks the list of test
     */
    public TestSuite(List<Check> checks, String name) {
        Preconditions.checkArg(checks != null, "The test suite can not be null");
        Preconditions.checkArg(name != null, "The test suite name can not be null");
        this.checks = checks;
        this.checkResults = new HashMap<>();
        this.name = name;
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

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "TestSuite: " + name + System.lineSeparator() + mapToString().indent(1);
    }

    @Override
    public Element getXMLElement(Document document) {
        Element testSuite = document.createElement("testSuite");
        testSuite.setAttribute("name", name);
        for(Check check : checks) {
            Element checkNode = check.getXMLElement(document);
            testSuite.appendChild(checkNode);
        }
        return testSuite;
    }

}
