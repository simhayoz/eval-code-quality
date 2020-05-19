package eval.code.quality.utils.reporter;

import eval.code.quality.position.MultiplePosition;
import eval.code.quality.position.Position;
import eval.code.quality.utils.description.Description;
import eval.code.quality.utils.description.DescriptionBuilder;
import eval.code.quality.utils.description.Descriptor;

import java.util.List;
import java.util.Map;

/**
 * Reporter for expected properties.
 *
 * @param <T> the type of the property
 */
public class ExpectedReporter<T> {

    /**
     * Called when the inferred property is unique.
     *
     * @param map      the map from which the inferred property was found
     * @param property the inferred property
     */
    public void doOnUniqueExpected(Map<T, List<Position>> map, T property) {
        // default method empty
    }

    /**
     * Report error when there is multiple inferred properties.
     *
     * @param map        the map from which the inferred properties were found
     * @param properties the list of possible properties
     * @return the reported error description
     */
    public Description reportMultipleExpected(Map<T, List<Position>> map, List<T> properties) {
        DescriptionBuilder builder = new DescriptionBuilder();
        properties.forEach(property -> {
            if (!map.get(property).isEmpty()) {
                builder.addPosition((map.get(property).size() > 1 ? new MultiplePosition(map.get(property)) : map.get(property).get(0)), new Descriptor().setWas(property.toString()));
            }
        });
        return builder.build();
    }
}
