package eval.code.quality.utils;

import eval.code.quality.position.Position;
import eval.code.quality.position.SinglePosition;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class ReportPositionTest {

    @Test void nullInputThrowsIllegalArgument() {
        Position position = new SinglePosition(1);
        assertThrows(IllegalArgumentException.class, () -> ReportPosition.at(null));
        assertThrows(IllegalArgumentException.class, () -> ReportPosition.at(null, "report"));
        assertThrows(IllegalArgumentException.class, () -> ReportPosition.at(position, null));
        assertThrows(IllegalArgumentException.class, () -> ReportPosition.at(null, "expected", "was"));
        assertThrows(IllegalArgumentException.class, () -> ReportPosition.at(position, null, "was"));
        assertThrows(IllegalArgumentException.class, () -> ReportPosition.at(position, "expected", null));
    }

    @Test void canCreateFromAt() {
        Position position = new SinglePosition(2);
        ReportPosition reportPosition = ReportPosition.at(position);
        assertThat(reportPosition.position, equalTo(position));
        assertThat(reportPosition.description, equalTo(""));
        reportPosition = ReportPosition.at(position, "description");
        assertThat(reportPosition.position, equalTo(position));
        assertThat(reportPosition.description, equalTo("description"));
        reportPosition = ReportPosition.at(position, "expected", "was");
        assertThat(reportPosition.position, equalTo(position));
        assertThat(reportPosition.description, equalTo("expected: expected, was: was"));
    }

    @Test void equalityWorksForSimplePosition() {
        Position position = new SinglePosition(1,2);
        Position position2 = new SinglePosition(1,2);
        Position position3 = new SinglePosition(3);
        ReportPosition reportPosition = ReportPosition.at(position);
        assertEquals(reportPosition, reportPosition);
        assertEquals(ReportPosition.at(position), ReportPosition.at(position2));
        assertNotEquals(ReportPosition.at(position), ReportPosition.at(position3));
        assertEquals(ReportPosition.at(position), ReportPosition.at(position));
        assertNotEquals(ReportPosition.at(position), null);
        assertNotEquals(ReportPosition.at(position), new Object());
    }

    @Test void toStringWorksForSimpleError() {
        Position position = new SinglePosition(1, 2);
        ReportPosition reportPosition = ReportPosition.at(position, "expected", "was");
        assertEquals(position.toString() + ": expected: expected, was: was", reportPosition.toString());
    }
}
