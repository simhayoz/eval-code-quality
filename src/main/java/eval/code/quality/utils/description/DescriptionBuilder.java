package eval.code.quality.utils.description;

import eval.code.quality.position.Position;

import java.util.ArrayList;
import java.util.List;

public class DescriptionBuilder {

    private final List<PositionDescription> positions;
    private final Descriptor descriptor;

    public DescriptionBuilder() {
        this.positions = new ArrayList<>();
        this.descriptor = new Descriptor();
    }

    public DescriptionBuilder addPosition(Position position) {
        positions.add(new PositionDescription(position, null));
        return this;
    }

    public DescriptionBuilder addPosition(Position position, Descriptor descriptor) {
        positions.add(new PositionDescription(position, descriptor));
        return this;
    }

    public DescriptionBuilder addToDescription(String toAdd) {
        descriptor.addToDescription(toAdd);
        return this;
    }

    public DescriptionBuilder setWas(String wasContent) {
        descriptor.setWas(wasContent);
        return this;
    }

    public DescriptionBuilder setExpected(String expectedContent) {
        descriptor.setExpected(expectedContent);
        return this;
    }

    public Description build() {
        return new Description(positions.isEmpty() ? null : positions, descriptor);
    }
}
