package eval.code.quality.utils.description;

import eval.code.quality.utils.XMLParsable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Optional;

public class Descriptor implements XMLParsable<Descriptor> {
    private final StringBuilder description;
    private String was;
    private String expected;

    public Descriptor() {
        this.description = new StringBuilder();
    }

    public Descriptor addToDescription(String toAdd) {
        description.append(toAdd);
        return this;
    }

    public Descriptor setWas(String wasContent) {
        was = wasContent;
        return this;
    }

    public Descriptor setExpected(String expectedContent) {
        expected = expectedContent;
        return this;
    }

    public Optional<String> getDescription() {
        if(description.length() == 0) {
            return Optional.empty();
        }
        return Optional.of(description.toString());
    }

    public Optional<String> getWas() {
        return Optional.ofNullable(was);
    }

    public Optional<String> getExpected() {
        return Optional.ofNullable(expected);
    }

    public String build() {
        StringBuilder builder = new StringBuilder();
        if(getDescription().isPresent()) {
            builder.append(description.toString());
        }
        if(getDescription().isPresent() && description.length() != 0 && (getExpected().isPresent() || getWas().isPresent())) {
            builder.append(": ");
        }
        String separator = "";
        if(was != null) {
            builder.append("was: ").append(was);
            separator = ", ";
        }
        if(expected != null) {
            builder.append(separator).append("expected: ").append(expected);
        }
        return builder.toString();
    }

    @Override
    public Element getXMLElement(Document document) {
        Element descriptor = document.createElement("descriptor");
        boolean hasElement = false;
        if(getDescription().isPresent()) {
            Element descriptionNode = document.createElement("description");
            descriptionNode.appendChild(document.createTextNode(description.toString()));
            descriptor.appendChild(descriptionNode);
            hasElement = true;
        }
        if(was != null) {
            Element wasNode = document.createElement("was");
            wasNode.appendChild(document.createTextNode(was));
            descriptor.appendChild(wasNode);
            hasElement = true;
        }
        if(expected != null) {
            Element expectedNode = document.createElement("expected");
            expectedNode.appendChild(document.createTextNode(expected));
            descriptor.appendChild(expectedNode);
            hasElement = true;
        }
        System.out.println(hasElement);
        return hasElement ? descriptor : null;
    }

    @Override
    public Descriptor getFromXML(Element xmlElement) {
        return null;
    }

//    public boolean isEmpty() {
//        return (getDescription().isEmpty() || description.length() == 0) && getExpected().isEmpty() && getWas().isEmpty();
//    }
}
