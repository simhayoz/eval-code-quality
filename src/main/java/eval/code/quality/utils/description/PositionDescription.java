package eval.code.quality.utils.description;

import eval.code.quality.position.Position;
import eval.code.quality.utils.Preconditions;
import eval.code.quality.utils.XMLParsable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Objects;

/**
 * Represents a position with description.
 */
public class PositionDescription implements XMLParsable {

    public final Position position;
    public final Descriptor descriptor;

    /**
     * Create a new {@code PositionDescription}.
     *
     * @param position   the position
     * @param descriptor the descriptor associated with the position
     */
    public PositionDescription(Position position, Descriptor descriptor) {
        Preconditions.checkArg(position != null, "Position can not be null");
        this.position = position;
        this.descriptor = descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PositionDescription that = (PositionDescription) o;
        return Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }

    @Override
    public String toString() {
        return position + (descriptor != null ? ": " + descriptor.prettyPrint() : "");
    }

    @Override
    public Element getXMLElement(Document document) {
        Element positionDescription = document.createElement("positionDescription");
        Element positionElement = position.getXMLElement(document);
        positionDescription.appendChild(positionElement);
        if (descriptor != null) {
            Element descriptorElement = descriptor.getXMLElement(document);
            if (descriptorElement != null) {
                positionDescription.appendChild(descriptorElement);
            }
        }
        return positionDescription;
    }

}
