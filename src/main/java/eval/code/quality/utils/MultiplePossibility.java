package eval.code.quality.utils;

import eval.code.quality.position.Position;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Deprecated
public class MultiplePossibility extends Error {
    private final Map<Position, String> positions;
    private final String description;

    private MultiplePossibility(Map<Position, String> positions, String description) {
        this.positions = positions;
        this.description = description;
    }

    public static MultiplePossibility at(List<Position> positions) {
        Map<Position, String> positionMap = new HashMap<>();
        positions.forEach(position -> positionMap.put(position, ""));
        return at(positionMap);
    }

    public static MultiplePossibility at(Map<Position, String> positions) {
        return at(positions, "");
    }

    public static MultiplePossibility at(Map<Position, String> positions, String description) {
        return new MultiplePossibility(positions, description);
    }

    @Override
    public String toString() {
        return "Multiple possibility: " + positions + " " + description;
    }
}
