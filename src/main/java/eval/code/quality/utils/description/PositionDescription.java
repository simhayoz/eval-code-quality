package eval.code.quality.utils.description;

import eval.code.quality.position.Position;
import eval.code.quality.utils.XMLParsable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Objects;

public class PositionDescription implements XMLParsable<PositionDescription> {

    public final Position position;
    public final Descriptor descriptor;

    public PositionDescription(Position position, Descriptor descriptor) {
        this.position = position;
        this.descriptor = descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionDescription that = (PositionDescription) o;
        return Objects.equals(position, that.position) &&
                Objects.equals(descriptor, that.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }

    @Override
    public String toString() {
        return position + (descriptor != null ? ": " + descriptor.build() : "");
    }

    @Override
    public Element getXMLElement(Document document) {
        Element positionDescription = document.createElement("positionDescription");
        Element positionElement = position.getXMLElement(document);
        positionDescription.appendChild(positionElement);
        Element descriptorElement = descriptor.getXMLElement(document);
        if(descriptorElement != null) {
            positionDescription.appendChild(descriptorElement);
        }
        return positionDescription;
    }

    @Override
    public PositionDescription getFromXML(Element xmlElement) {
        return null;
    }
}
