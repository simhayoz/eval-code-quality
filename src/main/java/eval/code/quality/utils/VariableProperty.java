package eval.code.quality.utils;

import java.util.Objects;

/**
 * Represents the property of a variable as described in the {@code Property} enum.
 * <p>The property does not consider the first and last character as it can be different from the overall style of
 * the variable and are treated separately in {@code CharacterProperty}</p>
 */
public class VariableProperty {

    public enum Property {
        AllUpper(true, false, false, false), // eg: THISISAVARIABLE
        AllUpperUnderscore(true, false, true, false), // THIS_IS_A_VARIABLE
        AllUpperDollar(true, false, false, true), // THIS$IS$A$VARIABLE
        CamelCase(false, false, false, false), // eg: thisIsAVariable
        AllLower(false, true, false, false), // eg: thisisavariable
        AllLowerUnderscore(false, true, true, false), // eg: this_is_a_variable
        AllLowerDollar(false, true, false, true), // eg: this$is$a$variable
        Underscore(false, false, true, false), // eg: this_Is_A_Variable
        Dollar(false, false, false, true), // eg: this$Is$A$Variable
        Empty(),
        None(); // Follow none of the previous convention

        private final boolean upper;
        private final boolean lower;
        private final boolean underscore;
        private final boolean dollar;

        Property() {
            this(true, true, true, true); // will not match anything
        }

        Property(boolean upper, boolean lower, boolean underscore, boolean dollar) {
            this.upper = upper;
            this.lower = lower;
            this.underscore = underscore;
            this.dollar = dollar;
        }

        /**
         * Create a new {@code Property} from the string.
         *
         * @param s the string
         * @return a new {@code Property} from the string
         */
        public static Property getFor(String s) {
            if (s.isEmpty()) {
                return Empty;
            }
            for (Property property : Property.values()) {
                if (testProperty(s, property)) {
                    return property;
                }
            }
            return None;
        }

        private static boolean testProperty(String s, Property property) {
            return (!property.upper ^ s.toUpperCase().equals(s))
                    && (!property.lower ^ s.toLowerCase().equals(s))
                    && (!property.underscore ^ s.contains("_"))
                    && (!property.dollar ^ s.contains("$"));
        }
    }

    public final Property property;

    /**
     * Create a new {@code VariableProperty} from the string.
     *
     * @param s the string
     */
    public VariableProperty(String s) {
        this.property = Property.getFor(s);
    }

    /**
     * Create a new {@code VariableProperty} from a property.
     *
     * @param property the property
     */
    public VariableProperty(Property property) {
        this.property = property;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableProperty that = (VariableProperty) o;
        return property == that.property;
    }

    @Override
    public int hashCode() {
        return property.ordinal();
    }

    @Override
    public String toString() {
        return this.property.toString();
    }
}
