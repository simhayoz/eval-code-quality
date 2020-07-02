package eval.code.quality.utils.reporter;

import eval.code.quality.position.Position;
import eval.code.quality.utils.description.Description;
import eval.code.quality.utils.description.DescriptionBuilder;

import java.util.List;
import java.util.Map;

/**
 * Named reporter for expected properties.
 *
 * @param <T> the type of the properties
 */
public class NamedExpectedReporter<T> extends ExpectedReporter<T> {
    private final String name;

    public NamedExpectedReporter(String name) {
        this.name = name;
    }

    @Override
    public Description reportMultipleExpected(Map<T, List<Position>> map, List<T> properties) {
        DescriptionBuilder builder = getReportMultipleExpectedBuilder(map, properties);
        if (builder.build().getPositions().isEmpty()) {
            return null;
        }
        builder.setExpected(properties + " to be all the same");
        builder.setName(name);
        return builder.build();
    }
}
