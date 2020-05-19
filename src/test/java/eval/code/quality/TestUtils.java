package eval.code.quality;

import eval.code.quality.position.Position;
import eval.code.quality.checks.Report;
import eval.code.quality.utils.description.Description;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils {

    public static void checkIsEmptyReport(Report report) {
        checkIsErrorEmpty(report);
        checkIsWarningEmpty(report);
    }

    public static void checkIsErrorEmpty(Report report) {
        assertThat(report.getErrors(), is(empty()));
    }

    public static void checkIsWarningEmpty(Report report) {
        assertThat(report.getWarnings(), is(empty()));
    }

    public static void checkNotIsErrorEmpty(Report report) {
        assertThat(report.getErrors(), is(not(empty())));
    }

    public static void checkNotIsWarningEmpty(Report report) {
        assertThat(report.getWarnings(), is(not(empty())));
    }

    public static List<List<Position>> getPositionFromReport(List<Description> report) {
        return report.stream().filter(err -> err.getPositions().isPresent()).map(err -> err.getPositions().get()).collect(Collectors.toList());
    }

    public static boolean containsPosition(List<Description> report, Position position) {
        return report.stream().filter(err -> err.getPositions().isPresent()).anyMatch(err -> err.getPositions().get().contains(position));
    }

    public static void reportContainsPosition(List<Description> report, Position position) {
        assertTrue(containsPosition(report, position), "Position " + position + " not found in report " + report);
    }

    public static void reportContainsPositions(List<Description> report, Position... positions) {
        for(Position position : positions) {
            if(!containsPosition(report, position)) {
                fail("Position " + position + " not found in report " + report);
            }
        }
    }

    public static void reportContainsOnlyPositions(List<Description> report, Position... positions) {
        long numberOfPosition = getPositionFromReport(report).stream().mapToLong(List::size).sum();
        if(numberOfPosition != positions.length) {
            fail("Not same size lists, should be " + positions.length + " but was " + numberOfPosition);
        }
        reportContainsPositions(report, positions);
    }
}
