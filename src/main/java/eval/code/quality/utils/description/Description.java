package eval.code.quality.utils.description;

import eval.code.quality.position.Position;
import eval.code.quality.utils.Preconditions;
import eval.code.quality.utils.XMLParsable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a description for an error or a warning.
 */
public class Description implements XMLParsable<Description> {

    private List<PositionDescription> positions;
    private Descriptor descriptor;

    /**
     * Create a new {@code Description}.
     *
     * @param positions  the position in the description
     * @param descriptor the descriptor representing different element of the description
     */
    public Description(List<PositionDescription> positions, Descriptor descriptor) {
        Preconditions.checkArg(descriptor != null, "Descriptor can not be null");
        this.positions = positions;
        this.descriptor = descriptor;
    }

    /**
     * Get positions.
     *
     * @return positions
     */
    public Optional<List<Position>> getPositions() {
        return Optional.ofNullable(positions).map(pos -> {
            List<Position> result = new ArrayList<>();
            pos.forEach(p -> result.add(p.position));
            return result;
        });
    }

    /**
     * Get positions with description.
     *
     * @return
     */
    public Optional<List<PositionDescription>> getPositionsWithDescription() {
        return Optional.ofNullable(positions);
    }

    /**
     * Get the descriptor attached to this description.
     *
     * @return the descriptor attached to this description
     */
    public Descriptor getDescriptor() {
        return descriptor;
    }

    private boolean cannotInferError() {
        return positions != null && positions.size() > 1;
    }

    /**
     * Pretty print the description.
     *
     * @return the pretty printed description
     */
    public String prettyPrintError() {
        StringBuilder builder = new StringBuilder();
        if (cannotInferError()) {
            builder.append("Cannot infer unique property from ")
                    .append(positions.size())
                    .append(" possible properties:")
                    .append(System.lineSeparator());
        }
        getPositionsWithDescription().ifPresent(positionDescriptions -> {
            for (PositionDescription positionDescription : positionDescriptions) {
                builder.append(positionDescription.toString());
                builder.append(System.lineSeparator());
            }
        });
        builder.append(descriptor.prettyPrint());
        return builder.toString();
    }

    @Override
    public String toString() {
        return prettyPrintError();
    }

    @Override
    public Element getXMLElement(Document document) {
        Element description = document.createElement("description");
        getPositionsWithDescription().ifPresent(positionDescriptions -> {
            for (PositionDescription positionDescription : positionDescriptions) {
                Element childPos = positionDescription.getXMLElement(document);
                description.appendChild(childPos);
            }
        });
        Element descriptorElement = descriptor.getXMLElement(document);
        if (descriptorElement != null) {
            description.appendChild(descriptorElement);
        }
        return description;
    }

}
