package eval.code.quality.utils;

import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * Represents the property of a character as described in the {@code Property} enum.
 */
public class CharacterProperty {

    public enum Property {
        Upper(Character::isUpperCase),
        Lower(Character::isLowerCase),
        Dollar(c -> c == '$'),
        Underscore(c -> c == '_'),
        Digit(Character::isDigit);

        private final Function<Character, Boolean> test;

        Property(Function<Character, Boolean> test) {
            this.test = test;
        }

        /**
         * Create a new {@code Property} from the char.
         *
         * @param c the char
         * @return a new {@code Property} from the char
         */
        public static Property getFor(char c) {
            for (Property characterProperty : Property.values()) {
                if (characterProperty.test.apply(c)) {
                    return characterProperty;
                }
            }
            throw new NoSuchElementException("The character cannot be in a variable");
        }
    }

    public final Property property;

    /**
     * Create a new {@code CharacterProperty} from a char.
     *
     * @param c the char to get the property from
     */
    public CharacterProperty(char c) {
        this.property = Property.getFor(c);
    }

    /**
     * Create a new {@code CharacterProperty} from a property.
     *
     * @param property the property
     */
    public CharacterProperty(Property property) {
        this.property = property;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterProperty that = (CharacterProperty) o;
        return this.property == that.property;
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
