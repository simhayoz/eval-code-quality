package eval.code.quality.checks;

import eval.code.quality.TestUtils;
import eval.code.quality.position.SinglePosition;
import eval.code.quality.utils.description.DescriptionBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestSuiteTest {

    @Test void nullTestsThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new TestSuite(null, "name"));
        assertThrows(IllegalArgumentException.class, () -> new TestSuite(new ArrayList<>(), null));
        assertThrows(IllegalArgumentException.class, () -> new TestSuite(null));
    }

    @Test void canCreateSimpleTestSuite() {
        List<Check> checks = new ArrayList<>();
        checks.add(new Check() {
            @Override
            protected void check() {
                // Do nothing
            }

            @Override
            public String getName() {
                return "First";
            }
        });
        checks.add(new Check() {
            @Override
            protected void check() {
                addError(new DescriptionBuilder().addPosition(new SinglePosition(1)));
            }

            @Override
            public String getName() {
                return "Second";
            }
        });
        TestSuite testSuite = new TestSuite(checks, "testSuite");
        Map<String, Report> report = testSuite.runChecks();
        assertThat(report.get("First").getErrors(), is(empty()));
        assertThat(report.get("First").getWarnings(), is(empty()));
        TestUtils.reportContainsOnlyPositions(report.get("Second").getErrors(), new SinglePosition(1));
        assertThat(report.get("Second").getWarnings(), is(empty()));
        report = testSuite.runChecks(true);
        assertThat(report.get("First").getErrors(), is(empty()));
        assertThat(report.get("First").getWarnings(), is(empty()));
        TestUtils.reportContainsOnlyPositions(report.get("Second").getErrors(), new SinglePosition(1));
        assertThat(report.get("Second").getWarnings(), is(empty()));
    }

    @Test void canCreateEmptyTestSuite() {
        TestSuite testSuite = new TestSuite("testSuites");
        testSuite.add(new Check() {
            @Override
            protected void check() {
                // Do nothing
            }

            @Override
            public String getName() {
                return "First";
            }
        });
        testSuite.add(new Check() {
            @Override
            protected void check() {
                addError(new DescriptionBuilder().addPosition(new SinglePosition(1)));
            }

            @Override
            public String getName() {
                return "Second";
            }
        });
        Map<String, Report> report = testSuite.runChecks();
        assertThat(report.get("First").getErrors(), is(empty()));
        assertThat(report.get("First").getWarnings(), is(empty()));
        TestUtils.reportContainsOnlyPositions(report.get("Second").getErrors(), new SinglePosition(1));
        assertThat(report.get("Second").getWarnings(), is(empty()));
        report = testSuite.runChecks(true);
        assertThat(report.get("First").getErrors(), is(empty()));
        assertThat(report.get("First").getWarnings(), is(empty()));
        TestUtils.reportContainsOnlyPositions(report.get("Second").getErrors(), new SinglePosition(1));
        assertThat(report.get("Second").getWarnings(), is(empty()));
    }

    @Test void toStringWorksForSimpleReport() {
        List<Check> checks = new ArrayList<>();
        checks.add(new Check() {
            @Override
            protected void check() {
                // Do nothing
            }

            @Override
            public String getName() {
                return "First";
            }
        });
        checks.add(new Check() {
            @Override
            protected void check() {
                addError(new DescriptionBuilder().addPosition(new SinglePosition(1)));
            }

            @Override
            public String getName() {
                return "Second";
            }
        });
        TestSuite testSuite = new TestSuite(checks, "testSuite");
        testSuite.runChecks();
        System.out.println(testSuite.toString());
        assertThat(testSuite.toString(), equalTo("TestSuite: testSuite\n" +
                " Test for Second: \n" +
                "  Error(s) reported: \n" +
                "   (line 1)\n" +
                "  \n" +
                "  Warning(s) reported: \n" +
                "   no warning found \n" +
                " \n" +
                " Test for First: \n" +
                "  Error(s) reported: \n" +
                "   no error found \n" +
                "  \n" +
                "  Warning(s) reported: \n" +
                "   no warning found \n"));
    }
}
