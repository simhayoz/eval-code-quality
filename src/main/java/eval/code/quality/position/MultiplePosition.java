package eval.code.quality.position;

import eval.code.quality.utils.Preconditions;

import java.util.*;

/**
 * Represents multiple position that are not necessarily successive.
 */
// TODO make a sorted set
public class MultiplePosition extends Position {
    private final List<Position> positions;

    public MultiplePosition() {
        this(new ArrayList<>());
    }

    /**
     * Create a new {@code MultiplePosition}.
     *
     * @param positions the list of {@code Position}
     */
    public MultiplePosition(List<Position> positions) {
        Preconditions.checkArg(positions != null, "The list of position cannot be null");
        this.positions = positions;
    }

    /**
     * Add a {@code Position} to the {@code MultiplePosition}.
     *
     * @param pos the position to add
     * @return whether the addition was successful or not
     */
    public boolean add(Position pos) {
        return positions.add(pos);
    }

    /**
     * Get the unmodifiable list of {@code Position}.
     *
     * @return the unmodifiable list of {@code Position}
     */
    public List<Position> getPositions() {
        return Collections.unmodifiableList(positions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiplePosition that = (MultiplePosition) o;
        return new HashSet<>(this.positions).equals(new HashSet<>(that.positions));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(positions);
    }

    @Override
    public String toString() {
        return positions.toString();
    }
}
