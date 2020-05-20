package eval.code.quality.position;

import eval.code.quality.utils.Preconditions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Objects;

/**
 * Decorator for {@code Position} to add name of the {@code ContentProvider}.
 */
public class NamePosition extends Position {
    public final String name;
    public final Position position;

    /**
     * Create a new {@code NamePosition}.
     *
     * @param name     the name of the position
     * @param position the position
     */
    public NamePosition(String name, Position position) {
        Preconditions.checkArg(name != null, "The name cannot be null");
        Preconditions.checkArg(position != null, "The position cannot be null");
        this.name = name;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamePosition that = (NamePosition) o;
        return this.name.equals(that.name) && this.position.equals(that.position);
    }

    @Override
    public int hashCode() {
        return 89 * Objects.hashCode(name) + Objects.hashCode(position);
    }

    @Override
    public String toString() {
        return name + " " + position;
    }

    @Override
    public Element getXMLElement(Document document) {
        Element namedPosition = document.createElement("namedPosition");
        namedPosition.setAttribute("name", name);
        Element pos = position.getXMLElement(document);
        namedPosition.appendChild(pos);
        return namedPosition;
    }

    @Override
    public Position getFromXML(Element xmlElement) {
        return null;
    }
}
