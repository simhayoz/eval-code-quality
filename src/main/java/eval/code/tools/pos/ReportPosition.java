package eval.code.tools.pos;

/**
 * Represents a position for error and warning
 */
public class ReportPosition {

    public final Position position;
    public final String report;

    private ReportPosition(Position position, String report) {
        this.position = position;
        this.report = report;
    }

    public static ReportPosition at(Position position) {
        return ReportPosition.at(position, "");
    }

    public static ReportPosition at(Position position, String report) {
        return new ReportPosition(position, report);
    }

    public static ReportPosition at(Position position, String expected, String was) {
        return new ReportPosition(position, "expected: " + expected + " was " + was);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof ReportPosition)) {
            return false;
        } else {
            ReportPosition s = (ReportPosition) obj;
            return this.position.equals(s.position);
        }
    }

    @Override
    public String toString() {
        return position + " " + report;
    }
}