package eval.code.quality.tests;

import eval.code.quality.position.SinglePosition;
import eval.code.quality.utils.ReportPosition;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestSuiteTest {

    @Test void nullTestsThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new TestSuite(null));
    }

    @Test void canCreateSimpleTestSuite() {
        List<eval.code.quality.tests.Test> tests = new ArrayList<>();
        tests.add(new eval.code.quality.tests.Test() {
            @Override
            protected void test() {
                // Do nothing
            }

            @Override
            protected String getName() {
                return "First";
            }
        });
        tests.add(new eval.code.quality.tests.Test() {
            @Override
            protected void test() {
                addError(ReportPosition.at(new SinglePosition(1)));
            }

            @Override
            protected String getName() {
                return "Second";
            }
        });
        TestSuite testSuite = new TestSuite(tests);
        Map<String, Report> report = testSuite.runTests();
        assertThat(report.get("First").getErrors(), is(empty()));
        assertThat(report.get("First").getWarnings(), is(empty()));
        assertThat(report.get("Second").getErrors(), hasItem(ReportPosition.at(new SinglePosition(1))));
        assertThat(report.get("Second").getWarnings(), is(empty()));
        report = testSuite.runTests(true);
        assertThat(report.get("First").getErrors(), is(empty()));
        assertThat(report.get("First").getWarnings(), is(empty()));
        assertThat(report.get("Second").getErrors(), hasItem(ReportPosition.at(new SinglePosition(1))));
        assertThat(report.get("Second").getWarnings(), is(empty()));
    }

    @Test void canCreateEmptyTestSuite() {
        TestSuite testSuite = new TestSuite();
        testSuite.add(new eval.code.quality.tests.Test() {
            @Override
            protected void test() {
                // Do nothing
            }

            @Override
            protected String getName() {
                return "First";
            }
        });
        testSuite.add(new eval.code.quality.tests.Test() {
            @Override
            protected void test() {
                addError(ReportPosition.at(new SinglePosition(1)));
            }

            @Override
            protected String getName() {
                return "Second";
            }
        });
        Map<String, Report> report = testSuite.runTests();
        assertThat(report.get("First").getErrors(), is(empty()));
        assertThat(report.get("First").getWarnings(), is(empty()));
        assertThat(report.get("Second").getErrors(), hasItem(ReportPosition.at(new SinglePosition(1))));
        assertThat(report.get("Second").getWarnings(), is(empty()));
        report = testSuite.runTests(true);
        assertThat(report.get("First").getErrors(), is(empty()));
        assertThat(report.get("First").getWarnings(), is(empty()));
        assertThat(report.get("Second").getErrors(), hasItem(ReportPosition.at(new SinglePosition(1))));
        assertThat(report.get("Second").getWarnings(), is(empty()));
    }

    @Test void toStringWorksForSimpleReport() {
        List<eval.code.quality.tests.Test> tests = new ArrayList<>();
        tests.add(new eval.code.quality.tests.Test() {
            @Override
            protected void test() {
                // Do nothing
            }

            @Override
            protected String getName() {
                return "First";
            }
        });
        tests.add(new eval.code.quality.tests.Test() {
            @Override
            protected void test() {
                addError(ReportPosition.at(new SinglePosition(1)));
            }

            @Override
            protected String getName() {
                return "Second";
            }
        });
        TestSuite testSuite = new TestSuite(tests);
        testSuite.runTests();
        System.out.println(testSuite.toString());
        assertThat(testSuite.toString(), equalTo("TestSuite: \n" +
                " Test for Second: \n" +
                "  Error(s) reported: \n" +
                "   (line 1): \n" +
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
