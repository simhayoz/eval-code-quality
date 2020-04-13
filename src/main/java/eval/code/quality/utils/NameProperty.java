package eval.code.quality.utils;

import java.util.Objects;

/**
 * Represents the properties of a variable naming.
 */
public class NameProperty {

    public final VariableProperty full_property;
    public final CharacterProperty start_property;
    public final CharacterProperty end_property;

    /**
     * Create a new {@code NameProperty} for the variable name.
     *
     * @param var_name the variable name
     */
    public NameProperty(String var_name) {
        Preconditions.checkArg(var_name != null && !var_name.isEmpty(), "The variable name cannot be null or empty");
        if (var_name.length() > 1) {
            this.full_property = new VariableProperty(var_name.substring(1, var_name.length() - 1));
        } else {
            this.full_property = new VariableProperty("");
        }
        this.start_property = new CharacterProperty(var_name.charAt(0));
        this.end_property = new CharacterProperty(var_name.charAt(var_name.length() - 1));
    }

    /**
     * Create a new {@code NameProperty} from properties.
     *
     * @param full_property  the property of the name
     * @param nameProperty the property to get the start and end property
     */
    public NameProperty(VariableProperty full_property, NameProperty nameProperty) {
        this.full_property = full_property;
        this.start_property = nameProperty.start_property;
        this.end_property = nameProperty.end_property;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NameProperty that = (NameProperty) o;
        return Objects.equals(full_property, that.full_property) &&
                Objects.equals(start_property, that.start_property) &&
                Objects.equals(end_property, that.end_property);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = 89 * hash + Objects.hashCode(full_property);
        hash = 89 * hash + Objects.hashCode(start_property);
        hash = 89 * hash + Objects.hashCode(end_property);
        return hash;
    }

    @Override
    public String toString() {
        return "{start:" + start_property + ",end:" + end_property + ",property:" + full_property + "}";
    }
}
