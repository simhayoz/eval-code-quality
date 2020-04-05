package eval.code.quality.utils;

import eval.code.quality.position.NamePosition;
import eval.code.quality.position.Position;

import java.util.List;

/**
 * Represents an error at a position with a description of the error.
 */
public class ReportPosition extends Error {
    public final NamePosition position;
    public final String description;

    private ReportPosition(NamePosition position, String description) {
        this.position = position;
        this.description = description;
    }

    /**
     * Create a new error at a position.
     *
     * @param position the position of the error
     * @return the new {@code ReportPosition}
     */
    public static ReportPosition at(NamePosition position) {
        return ReportPosition.at(position, "");
    }

    /**
     * Create a new error at a position with a description.
     *
     * @param position the position of the error
     * @param report   the description of the error
     * @return the new {@code ReportPosition}
     */
    public static ReportPosition at(NamePosition position, String report) {
        return new ReportPosition(position, report);
    }

    /**
     * Create a new error at a position with a description.
     *
     * @param position the position of the error
     * @param expected the description of what was expected
     * @param was      the description of what was given
     * @return the new {@code ReportPosition}
     */
    public static ReportPosition at(NamePosition position, String expected, String was) {
        return ReportPosition.at(position, "expected: " + expected + " was: " + was);
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
        return position + ": " + description;
    }
}
