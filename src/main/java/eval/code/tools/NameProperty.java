package eval.code.tools;

public class NameProperty {

    public enum FProperty {
        AllUpper, // eg: THISISAVARIABLE
        AllUpperUnderscore, // THIS_IS_A_VARIABLE
        CamelCase, // eg: thisIsAVariable
        AllLowerUnderscore, // eg: this_is_a_variable
        AllLower, // eg: thisisavariable
        Underscore, // eg: this_Is_A_Variable
        None // Follow none of the previous convention
    }

    public enum PProperty {
        Upper, Lower, Underscore
    }

    public final FProperty full_property;
    public final PProperty start_property;
    public final PProperty end_property;

    private NameProperty(String var_name) {
        if (var_name == null || var_name.isEmpty()) {
            throw new IllegalArgumentException("The variable name cannot be null or empty");
        }
        start_property = setCharPoperty(var_name.charAt(0));
        end_property = setCharPoperty(var_name.charAt(var_name.length() - 1));
        full_property = getFullProperty(var_name);
    }

    public static NameProperty getFor(String var_name) {
        return new NameProperty(var_name);
    }

    private PProperty setCharPoperty(char c) {
        return (c == '_') ? PProperty.Underscore : (Character.isLowerCase(c)) ? PProperty.Lower : PProperty.Upper;
    }

    private FProperty getFullProperty(String v) {
        if (v.length() <= 2) {
            return FProperty.None;
        } else if (v.substring(1, v.length() - 2).contains("_")) { // has underscore
            if (v.toLowerCase().equals(v)) {
                return FProperty.AllLowerUnderscore;
            } else if (v.toUpperCase().equals(v)) {
                return FProperty.AllUpperUnderscore;
            } else {
                return FProperty.Underscore;
            }
        } else { // no underscore
            if (v.toLowerCase().equals(v)) {
                return FProperty.AllLower;
            } else if (v.toUpperCase().equals(v)) {
                return FProperty.AllUpper;
            } else {
                int upper_b2b = 0;
                for (int i = 0; i < v.length(); ++i) {
                    if (Character.isUpperCase(v.charAt(i))) {
                        ++upper_b2b;
                    } else {
                        upper_b2b = 0;
                    }
                    if (upper_b2b >= 3) {
                        return FProperty.None;
                    }
                }
                return FProperty.CamelCase;
            }
        }
    }

    @Override
    public int hashCode() {
        return full_property.hashCode() + start_property.hashCode() + end_property.hashCode();
    }

    @Override
    public String toString() {
        return "[start:" + start_property + ",end:" + end_property + ",property:" + full_property + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof NameProperty)) {
            return false;
        } else {
            NameProperty n = (NameProperty) obj;
            return this.full_property == n.full_property && this.start_property == n.start_property
                    && this.end_property == n.end_property;
        }
    }
} 