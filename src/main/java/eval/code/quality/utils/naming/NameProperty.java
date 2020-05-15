package eval.code.quality.utils.naming;

import eval.code.quality.utils.Preconditions;

import java.util.Objects;

/**
 * Represents the properties of a variable naming.
 */
public class NameProperty {

    public final VariableProperty fullProperty;
    public final CharacterProperty startProperty;
    public final CharacterProperty endProperty;

    /**
     * Create a new {@code NameProperty} for the variable name.
     *
     * @param var_name the variable name
     */
    public NameProperty(String var_name) {
        Preconditions.checkArg(var_name != null && !var_name.isEmpty(), "The variable name cannot be null or empty");
        this.startProperty = new CharacterProperty(var_name.charAt(0));
        this.endProperty = new CharacterProperty(var_name.charAt(var_name.length() - 1));
        int indexEnd = this.endProperty.isOther() ? var_name.length() : var_name.length() - 1;
        if(var_name.length() > 1) {
            this.fullProperty = new VariableProperty(var_name.substring(1, indexEnd));
        } else {
            this.fullProperty = new VariableProperty("");
        }
    }

    /**
     * Create a new {@code NameProperty} from properties.
     *
     * @param fullProperty  the property of the name
     * @param nameProperty the property to get the start and end property
     */
    public NameProperty(VariableProperty fullProperty, NameProperty nameProperty) {
        this.fullProperty = fullProperty;
        this.startProperty = nameProperty.startProperty;
        this.endProperty = nameProperty.endProperty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NameProperty that = (NameProperty) o;
        return Objects.equals(fullProperty, that.fullProperty) &&
                Objects.equals(startProperty, that.startProperty) &&
                ((endProperty.isOther() && that.endProperty.isOther()) || Objects.equals(endProperty, that.endProperty));
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = 89 * hash + Objects.hashCode(fullProperty);
        hash = 89 * hash + Objects.hashCode(startProperty);
        hash = 89 * hash + Objects.hashCode(endProperty);
        return hash;
    }

    @Override
    public String toString() {
        String suffix = "";
        if(!endProperty.isOther()) {
            suffix = "end:" + endProperty + ",";
        }
        return "{start:" + startProperty + "," + suffix + "property:" + fullProperty + "}";
    }
}
