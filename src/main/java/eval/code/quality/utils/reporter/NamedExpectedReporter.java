package eval.code.quality.utils.reporter;

import eval.code.quality.position.MultiplePosition;
import eval.code.quality.position.Position;
import eval.code.quality.utils.description.Description;
import eval.code.quality.utils.description.DescriptionBuilder;
import eval.code.quality.utils.description.Descriptor;

import java.util.List;
import java.util.Map;

public class NamedExpectedReporter<T> extends ExpectedReporter<T> {
    private final String name;

    public NamedExpectedReporter(String name) {
        this.name = name;
    }

    @Override
    public Description reportMultipleExpected(Map<T, List<Position>> map, List<T> properties) {
        DescriptionBuilder builder = new DescriptionBuilder();
        properties.forEach(property -> {
            if(!map.get(property).isEmpty()) {
                builder.addPosition((map.get(property).size() > 1 ? new MultiplePosition(map.get(property)) : map.get(property).get(0)), new Descriptor().setWas(property.toString()));
            }
        });
        if(builder.build().getPositions().isEmpty()) {
            return null;
        }
        builder.setExpected(properties + " to be all the same for: " + name);
        return builder.build();
    }
}
