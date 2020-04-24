package eval.code.quality.utils;

import eval.code.quality.position.MultiplePosition;
import eval.code.quality.position.Position;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NamedExpectedReporter<T> extends ExpectedReporter<T> {
    private final String name;

    public NamedExpectedReporter(String name) {
        this.name = name;
    }

    @Override
    public Error reportMultipleExpected(Map<T, List<Position>> map, List<T> properties) {
        Map<Position, String> intended = new HashMap<>();
        properties.forEach(property -> {
            if(!map.get(property).isEmpty()) {
                intended.put(
                        (map.get(property).size() > 1 ? new MultiplePosition(map.get(property)) : map.get(property).get(0)), property.toString());
            }
        });
        if(!intended.isEmpty()) {
            return MultiplePossibility.at(intended, "Multiple possible properties: " + properties + " for: " + name + ", should be all the same");
        }
        return null;
    }
}
