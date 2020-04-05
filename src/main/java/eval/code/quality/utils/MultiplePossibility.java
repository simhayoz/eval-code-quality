package eval.code.quality.utils;

import eval.code.quality.position.NamePosition;
import eval.code.quality.position.Position;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultiplePossibility extends Error {
    private final Map<NamePosition, String> positions;
    private final String description;

    private MultiplePossibility(Map<NamePosition, String> positions, String description) {
        this.positions = positions;
        this.description = description;
    }

    public static MultiplePossibility at(List<NamePosition> positions) {
        Map<NamePosition, String> positionMap = new HashMap<>();
        positions.forEach(position -> positionMap.put(position, ""));
        return at(positionMap);
    }

    public static MultiplePossibility at(Map<NamePosition, String> positions) {
        return at(positions, "");
    }

    public static MultiplePossibility at(Map<NamePosition, String> positions, String description) {
        return new MultiplePossibility(positions, description);
    }

    @Override
    public String toString() {
        return description + " but was : " + System.lineSeparator() + positions.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue()).collect(Collectors.joining(System.lineSeparator())).indent(1);
    }
}
