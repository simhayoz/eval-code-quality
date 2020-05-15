package eval.code.quality.tests;

import eval.code.quality.position.SinglePosition;
import eval.code.quality.utils.Error;
import eval.code.quality.utils.ReportPosition;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;

public class ReportTest {

//    @Test void emptyReportGetEmptyErrors() {
//        assertThat(new Report().getErrors(), is(empty()));
//        assertThat(new Report().getWarnings(), is(empty()));
//    }
//
//    @Test void addErrorsToReportAreAdded() {
//        Report report = new Report();
//        ReportPosition error1 = ReportPosition.at(new SinglePosition(1));
//        ReportPosition error2 = ReportPosition.at(new SinglePosition(2));
//        report.addError(error1);
//        report.addError(error2);
//        assertThat(report.getErrors(), Matchers.<Collection<Error>>allOf(
//                hasItems(is(ReportPosition.at(new SinglePosition(1))),
//                        is(ReportPosition.at(new SinglePosition(2)))),
//                hasSize(2)));
//        assertThat(report.getWarnings(), is(empty()));
//    }
//
//    @Test void addWarningsToReportAreAdded() {
//        Report report = new Report();
//        ReportPosition error1 = ReportPosition.at(new SinglePosition(1));
//        ReportPosition error2 = ReportPosition.at(new SinglePosition(2));
//        report.addWarning(error1);
//        report.addWarning(error2);
//        assertThat(report.getWarnings(), Matchers.<Collection<Error>>allOf(
//                hasItems(is(ReportPosition.at(new SinglePosition(1))),
//                        is(ReportPosition.at(new SinglePosition(2)))),
//                hasSize(2)));
//        assertThat(report.getErrors(), is(empty()));
//    }
//
//    @Test void toStringForEmptyReport() {
//        assertThat(new Report().toString(),
//                equalTo("Error(s) reported: \n" +
//                        " no error found \n" +
//                        "\n" +
//                        "Warning(s) reported: \n" +
//                        " no warning found \n"));
//    }
//
//    @Test void toStringForWarningsAndErrors() {
//        Report report = new Report();
//        ReportPosition error1 = ReportPosition.at(new SinglePosition(1));
//        ReportPosition error2 = ReportPosition.at(new SinglePosition(2));
//        report.addError(error1);
//        report.addError(error2);
//        report.addWarning(error1);
//        report.addWarning(error2);
//        assertThat(report.toString(),
//                equalTo("Error(s) reported: \n" +
//                        " (line 1): \n" +
//                        " (line 2): \n" +
//                        "\n" +
//                        "Warning(s) reported: \n" +
//                        " (line 1): \n" +
//                        " (line 2): \n"));
//    }
}
