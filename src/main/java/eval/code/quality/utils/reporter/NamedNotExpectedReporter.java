package eval.code.quality.utils.reporter;

import eval.code.quality.position.Position;
import eval.code.quality.utils.description.Description;
import eval.code.quality.utils.description.DescriptionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Named reporter for unexpected properties.
 *
 * @param <T> the type of the properties
 */
public class NamedNotExpectedReporter<T> extends NotExpectedReporter<T> {
    private final String name;

    public NamedNotExpectedReporter(String name) {
        this.name = name;
    }

    @Override
    public List<Description> reportNotExpected(Map<T, List<Position>> map, List<T> expected, List<T> notExpected) {
        List<Description> errors = new ArrayList<>();
        List<Description> prevErrors = super.reportNotExpected(map, expected, notExpected);
        prevErrors.forEach(error -> error.getPositionsWithDescription().ifPresent(pos -> {
            pos.forEach(p -> errors.add(new DescriptionBuilder().addPosition(p.position, p.descriptor.addToDescription(name)).build()));
        }));
        return errors;
    }
}
