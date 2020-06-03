package eval.code.quality.utils.reporter;

import eval.code.quality.position.MultiplePosition;
import eval.code.quality.position.Position;
import eval.code.quality.utils.description.Description;
import eval.code.quality.utils.description.DescriptionBuilder;
import eval.code.quality.utils.description.Descriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Reporter for unexpected properties.
 *
 * @param <T> the type of the properties
 */
public class NotExpectedReporter<T> {

    /**
     * Called when expected and not expected properties are known
     *
     * @param map         the map from which was inferred the properties
     * @param expected    the list of expected properties
     * @param notExpected the list of unexpected properties
     */
    public void doOnNotExpected(Map<T, List<Position>> map, List<T> expected, List<T> notExpected) {
        // default method do nothing
    }

    /**
     * Report error for unexpected properties.
     *
     * @param map         the map from which was inferred the properties
     * @param expected    the list of expected properties
     * @param notExpected the list of unexpected properties
     * @return the list of reported error description
     */
    public List<Description> reportNotExpected(Map<T, List<Position>> map, List<T> expected, List<T> notExpected) {
        List<Description> descriptions = new ArrayList<>();
        for (T element : notExpected) {
            if (!map.get(element).isEmpty()) {
                String expectedString = expected.size() == 1 ? expected.get(0).toString() : expected.toString();
                if (map.get(element).size() > 1) {
                    MultiplePosition positions = new MultiplePosition();
                    map.get(element).forEach(positions::add);
                    descriptions.add(new DescriptionBuilder()
                            .addPosition(positions, new Descriptor().setExpected(expectedString).setWas(element.toString()))
                            .build());
                } else {
                    descriptions.add(new DescriptionBuilder()
                            .addPosition(map.get(element).get(0), new Descriptor().setExpected(expectedString).setWas(element.toString()))
                            .build());
                }
            }
        }
        return descriptions;
    }

}
