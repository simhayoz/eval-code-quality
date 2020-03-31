package eval.code.quality.position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents multiple position that are not necessarily successive.
 */
public class MultiplePosition extends Position {
    private final List<Position> positions;

    public MultiplePosition() {
        this(new ArrayList<>());
    }

    public MultiplePosition(List<Position> positions) {
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
    public String toString() {
        return positions.toString();
    }
}
