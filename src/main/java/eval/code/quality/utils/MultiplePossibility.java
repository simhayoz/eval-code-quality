package eval.code.quality.utils;

import eval.code.quality.position.Position;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents an error at multiple positions with a description of the error.
 */
public class MultiplePossibility extends Error {
    public final Map<Position, String> positions;
    public final String description;

    private MultiplePossibility(Map<Position, String> positions, String description) {
        this.positions = positions;
        this.description = description;
    }

    /**
     * Create a new {@code MultiplePossibility} from a list of positions.
     *
     * @param positions the list of positions
     * @return a new {@code MultiplePossibility} from a list of positions
     */
    public static MultiplePossibility at(List<Position> positions) {
        Preconditions.checkArg(positions != null, "The list of positions can not be null");
        Map<Position, String> positionMap = new HashMap<>();
        positions.forEach(position -> positionMap.put(position, ""));
        return at(positionMap);
    }

    /**
     * Create a new {@code MultiplePossibility} from a map of positions and error description.
     *
     * @param positions the map of positions and error description
     * @return a new {@code MultiplePossibility} from a map of positions and error description
     */
    public static MultiplePossibility at(Map<Position, String> positions) {
        Preconditions.checkArg(positions != null, "The map of positions and error can not be null");
        return at(positions, "");
    }

    /**
     * Create a new {@code MultiplePossibility} from a map of positions and error description and a general description of the error.
     *
     * @param positions   the map of positions and error description
     * @param description the general description of the error
     * @return a new {@code MultiplePossibility} from a map of positions and error description and a general description of the error
     */
    public static MultiplePossibility at(Map<Position, String> positions, String description) {
        Preconditions.checkArg(positions != null, "The map of positions and error can not be null");
        Preconditions.checkArg(description != null, "The description can not be null");
        return new MultiplePossibility(positions, description);
    }

    @Override
    public String toString() {
        return description + " but was: " + System.lineSeparator() + positions.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue()).collect(Collectors.joining(System.lineSeparator())).indent(1);
    }
}
