package eval.code.quality.tests;

import eval.code.quality.TestUtils;
import eval.code.quality.position.SinglePosition;
import eval.code.quality.utils.description.Description;
import eval.code.quality.utils.description.DescriptionBuilder;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ReportTest {

    @Test void emptyReportGetEmptyErrors() {
        TestUtils.checkIsEmptyReport(new Report());
    }

    @Test void addErrorsToReportAreAdded() {
        Report report = new Report();
        Description error1 = new DescriptionBuilder().addPosition(new SinglePosition(1)).build();
        Description error2 = new DescriptionBuilder().addPosition(new SinglePosition(2)).build();
        report.addError(error1);
        report.addError(error2);
        TestUtils.reportContainsOnlyPositions(report.getErrors(), new SinglePosition(1), new SinglePosition(2));
        TestUtils.checkIsWarningEmpty(report);
    }

    @Test void addWarningsToReportAreAdded() {
        Report report = new Report();
        Description error1 = new DescriptionBuilder().addPosition(new SinglePosition(1)).build();
        Description error2 = new DescriptionBuilder().addPosition(new SinglePosition(2)).build();
        report.addWarning(error1);
        report.addWarning(error2);
        TestUtils.reportContainsOnlyPositions(report.getWarnings(), new SinglePosition(1), new SinglePosition(2));
        TestUtils.checkIsErrorEmpty(report);
    }

    @Test void toStringForEmptyReport() {
        assertThat(new Report().toString(),
                equalTo("Error(s) reported: \n" +
                        " no error found \n" +
                        "\n" +
                        "Warning(s) reported: \n" +
                        " no warning found \n"));
    }

    @Test void toStringForWarningsAndErrors() {
        Report report = new Report();
        Description error1 = new DescriptionBuilder().addPosition(new SinglePosition(1)).build();
        Description error2 = new DescriptionBuilder().addPosition(new SinglePosition(2)).build();
        report.addError(error1);
        report.addError(error2);
        report.addWarning(error1);
        report.addWarning(error2);
        assertThat(report.toString(),
                equalTo("Error(s) reported: \n" +
                        " (line 1)\n" +
                        " (line 2)\n" +
                        "\n" +
                        "Warning(s) reported: \n" +
                        " (line 1)\n" +
                        " (line 2)\n"));
    }
}
