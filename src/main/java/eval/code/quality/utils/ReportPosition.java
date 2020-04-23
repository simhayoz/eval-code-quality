package eval.code.quality.utils;

import eval.code.quality.position.Position;

/**
 * Represents an error at a position with a description of the error.
 */
public class ReportPosition extends Error {
    public final Position position;
    public final String description;

    private ReportPosition(Position position, String description) {
        this.position = position;
        this.description = description;
    }

    /**
     * Create a new error at a position.
     *
     * @param position the position of the error
     * @return the new {@code ReportPosition}
     */
    public static ReportPosition at(Position position) {
        Preconditions.checkArg(position != null, "Position can not be null");
        return ReportPosition.at(position, "");
    }

    /**
     * Create a new error at a position with a description.
     *
     * @param position the position of the error
     * @param report   the description of the error
     * @return the new {@code ReportPosition}
     */
    public static ReportPosition at(Position position, String report) {
        Preconditions.checkArg(position != null, "Position can not be null");
        Preconditions.checkArg(report != null, "Report string can not be null");
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
    public static ReportPosition at(Position position, String expected, String was) {
        Preconditions.checkArg(position != null, "Position can not be null");
        Preconditions.checkArg(expected != null, "Expected string can not be null");
        Preconditions.checkArg(was != null, "Was string can not be null");
        return ReportPosition.at(position, "expected: " + expected + ", was: " + was);
    }

    public static ReportPosition at(Position position, String description, String expected, String was) {
        Preconditions.checkArg(position != null, "Position can not be null");
        Preconditions.checkArg(description != null, "Description can not be null");
        Preconditions.checkArg(expected != null, "Expected string can not be null");
        Preconditions.checkArg(was != null, "Was string can not be null");
        return ReportPosition.at(position, description + " expected: " + expected + ", was: " + was);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportPosition that = (ReportPosition) o;
        return this.position.equals(that.position);
    }

    @Override
    public String toString() {
        return position + ": " + description;
    }
}
