package eval.code.quality;

import eval.code.quality.position.Position;
import eval.code.quality.tests.Report;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils {

    public static List<List<Position>> getPositionFromReport(Report report) {
        return report.getErrors().stream().filter(err -> err.getPositions().isPresent()).map(err -> err.getPositions().get()).collect(Collectors.toList());
    }

    public static boolean containsPosition(Report report, Position position) {
        return report.getErrors().stream().filter(err -> err.getPositions().isPresent()).anyMatch(err -> err.getPositions().get().contains(position));
    }

    public static void reportContainsPosition(Report report, Position position) {
        assertTrue(containsPosition(report, position), "Position " + position + " not found in report " + report);
    }

    public static void reportContainsPositions(Report report, Position... positions) {
        for(Position position : positions) {
            if(!containsPosition(report, position)) {
                fail("Position " + position + " not found in report " + report);
            }
        }
    }

    public static void reportContainsOnlyPositions(Report report, Position... positions) {
        long numberOfPosition = getPositionFromReport(report).stream().mapToLong(List::size).sum();
        if(numberOfPosition != positions.length) {
            fail("Not same size lists, should be " + positions.length + " but was " + numberOfPosition + "->" +report);
        }
        reportContainsPositions(report, positions);
    }
}
