package eval.code.quality.utils;

import eval.code.quality.position.Position;

/**
 * Represents an error by a difference between two or more positions.
 * <p>This is used when we cannot determine which position was an error and which was not, but we know by the
 * difference between the two that an error was found</p>
 */
@Deprecated
public class DifferencePosition extends Error {
    public final Position firstPosition;
    public final Position secondPosition;
    public final String description;

    private DifferencePosition(Position firstPosition, Position secondPosition, String description) {
        this.firstPosition = firstPosition;
        this.secondPosition = secondPosition;
        this.description = description;
    }

    /**
     * Create a new error between to position with no description.
     *
     * @param firstPosition  the {@code Position} of the first element
     * @param secondPosition the {@code Position} of the second element
     * @return the new {@code DifferencePosition}
     */
    public static DifferencePosition at(Position firstPosition, Position secondPosition) {
        return DifferencePosition.at(firstPosition, secondPosition, "");
    }

    /**
     * Create a new error between to position with a description.
     *
     * @param firstPosition  the {@code Position} of the first element
     * @param secondPosition the {@code Position} of the second element
     * @param report         the description of the error
     * @return the new {@code DifferencePosition}
     */
    public static DifferencePosition at(Position firstPosition, Position secondPosition, String report) {
        return new DifferencePosition(firstPosition, secondPosition, report);
    }

    /**
     * Create a new error between to position with a description.
     *
     * @param firstPosition     the {@code Position} of the first element
     * @param secondPosition    the {@code Position} of the second element
     * @param firstDescription  the description of the first element
     * @param secondDescription the description of the second element
     * @return the new {@code DifferencePosition}
     */
    public static DifferencePosition at(Position firstPosition, Position secondPosition, String firstDescription, String secondDescription) {
        return DifferencePosition.at(firstPosition, secondPosition, "was in first: " + firstDescription + " was in second: " + secondDescription);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof DifferencePosition)) {
            return false;
        } else {
            DifferencePosition s = (DifferencePosition) obj;
            return this.firstPosition.equals(s.firstPosition) && this.secondPosition.equals(s.secondPosition);
        }
    }

    @Override
    public String toString() {
        return firstPosition + " =!= " + secondPosition + " " + description;
    }
}
