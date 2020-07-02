package eval.code.quality.utils.description;

import eval.code.quality.position.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for description.
 */
public class DescriptionBuilder {

    private final List<PositionDescription> positions;
    private final Descriptor descriptor;
    private String name;

    /**
     * Create a new {@code DescriptionBuilder}.
     */
    public DescriptionBuilder() {
        this.positions = new ArrayList<>();
        this.descriptor = new Descriptor();
    }

    /**
     * Add a position to the description.
     *
     * @param position the position to add
     * @return this
     */
    public DescriptionBuilder addPosition(Position position) {
        positions.add(new PositionDescription(position, null));
        return this;
    }

    /**
     * Add a position with descriptor to the description.
     *
     * @param position   the position to add
     * @param descriptor the descriptor associated with the position
     * @return this
     */
    public DescriptionBuilder addPosition(Position position, Descriptor descriptor) {
        positions.add(new PositionDescription(position, descriptor));
        return this;
    }

    /**
     * Set the name of the current description.
     * @param name the name of the current description
     * @return this
     */
    public DescriptionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Add to the description.
     *
     * @param toAdd string to add
     * @return this
     */
    public DescriptionBuilder addToDescription(String toAdd) {
        descriptor.addToDescription(toAdd);
        return this;
    }

    /**
     * Set was string description.
     *
     * @param wasContent was string description
     * @return this
     */
    public DescriptionBuilder setWas(String wasContent) {
        descriptor.setWas(wasContent);
        return this;
    }

    /**
     * Set expected string description.
     *
     * @param expectedContent expected string description
     * @return this
     */
    public DescriptionBuilder setExpected(String expectedContent) {
        descriptor.setExpected(expectedContent);
        return this;
    }

    /**
     * Construct a {@code Description} from the current builder.
     *
     * @return the constructed {@code Description}
     */
    public Description build() {
        return new Description(positions.isEmpty() ? null : positions, descriptor, name);
    }
}
