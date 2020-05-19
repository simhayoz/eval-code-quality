package eval.code.quality.utils.description;

import java.util.Optional;

public class Descriptor {
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

//    public boolean isEmpty() {
//        return (getDescription().isEmpty() || description.length() == 0) && getExpected().isEmpty() && getWas().isEmpty();
//    }
}
