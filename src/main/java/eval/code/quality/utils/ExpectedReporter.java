package eval.code.quality.utils;

import eval.code.quality.position.MultiplePosition;
import eval.code.quality.position.Position;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpectedReporter<T> {

    public void doOnUniqueExpected(Map<T, List<Position>> map, T property) {
        // default method empty
    }

    public Error reportMultipleExpected(Map<T, List<Position>> map, List<T> properties) {
        Map<Position, String> intended = new HashMap<>();
        properties.forEach(property -> {
            if(!map.get(property).isEmpty()) {
                intended.put(
                        (map.get(property).size() > 1 ? new MultiplePosition(map.get(property)) : map.get(property).get(0)), property.toString());
            }
        });
        if(!intended.isEmpty()) {
            return MultiplePossibility.at(intended, "Multiple possible properties: " + properties + ", should be all the same");
        }
        return null;
    }
}
