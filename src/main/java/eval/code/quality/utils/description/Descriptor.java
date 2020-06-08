package eval.code.quality.utils.description;

import eval.code.quality.utils.XMLParsable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Optional;

/**
 * Represent a descriptor with description, was and expected value.
 */
public class Descriptor implements XMLParsable {
    private final StringBuilder description;
    private String was;
    private String expected;

    /**
     * Create a new {@code Descriptor}.
     */
    public Descriptor() {
        this.description = new StringBuilder();
    }

    /**
     * Add to the description.
     *
     * @param toAdd string to add
     * @return this
     */
    public Descriptor addToDescription(String toAdd) {
        description.append(toAdd);
        return this;
    }

    /**
     * Set was string description.
     *
     * @param wasContent was string description
     * @return this
     */
    public Descriptor setWas(String wasContent) {
        was = wasContent;
        return this;
    }

    /**
     * Set expected string description.
     *
     * @param expectedContent expected string description
     * @return this
     */
    public Descriptor setExpected(String expectedContent) {
        expected = expectedContent;
        return this;
    }

    /**
     * Get description.
     *
     * @return description
     */
    public Optional<String> getDescription() {
        if (description.length() == 0) {
            return Optional.empty();
        }
        return Optional.of(description.toString());
    }

    /**
     * Get was value.
     *
     * @return was value
     */
    public Optional<String> getWas() {
        return Optional.ofNullable(was);
    }

    /**
     * Get expected value.
     *
     * @return expected value
     */
    public Optional<String> getExpected() {
        return Optional.ofNullable(expected);
    }

    /**
     * Pretty print this descriptor.
     *
     * @return this pretty printed descriptor
     */
    public String prettyPrint() {
        StringBuilder builder = new StringBuilder();
        if (getDescription().isPresent()) {
            builder.append(description.toString());
        }
        if (getDescription().isPresent() && description.length() != 0 && (getExpected().isPresent() || getWas().isPresent())) {
            builder.append(": ");
        }
        String separator = "";
        if (was != null) {
            builder.append("was: ").append(was);
            separator = ", ";
        }
        if (expected != null) {
            builder.append(separator).append("expected: ").append(expected);
        }
        return builder.toString();
    }

    @Override
    public Element getXMLElement(Document document) {
        Element descriptor = document.createElement("descriptor");
        boolean hasElement = false;
        if (getDescription().isPresent()) {
            Element descriptionNode = document.createElement("description");
            descriptionNode.appendChild(document.createTextNode(description.toString()));
            descriptor.appendChild(descriptionNode);
            hasElement = true;
        }
        if (was != null) {
            Element wasNode = document.createElement("was");
            wasNode.appendChild(document.createTextNode(was));
            descriptor.appendChild(wasNode);
            hasElement = true;
        }
        if (expected != null) {
            Element expectedNode = document.createElement("expected");
            expectedNode.appendChild(document.createTextNode(expected));
            descriptor.appendChild(expectedNode);
            hasElement = true;
        }
        return hasElement ? descriptor : null;
    }
}
