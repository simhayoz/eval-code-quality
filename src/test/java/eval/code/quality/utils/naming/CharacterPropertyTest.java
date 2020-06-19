package eval.code.quality.utils.naming;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class CharacterPropertyTest {

    @Test void wrongCharThrowsNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> new CharacterProperty('/'));
    }

    @Test void charMatchesToRightProperty() {
        assertEquals(CharacterProperty.Property.Upper, new CharacterProperty('C').property);
        assertEquals(CharacterProperty.Property.Upper, new CharacterProperty('D').property);
        assertEquals(CharacterProperty.Property.Lower, new CharacterProperty('a').property);
        assertEquals(CharacterProperty.Property.Lower, new CharacterProperty('e').property);
        assertEquals(CharacterProperty.Property.Dollar, new CharacterProperty('$').property);
        assertEquals(CharacterProperty.Property.Underscore, new CharacterProperty('_').property);
        assertEquals(CharacterProperty.Property.Digit, new CharacterProperty('2').property);
        assertEquals(CharacterProperty.Property.Digit, new CharacterProperty('3').property);
    }

    @Test void equalsWorksForDifferentProperties() {
        CharacterProperty upper = new CharacterProperty('C');
        CharacterProperty upper2 = new CharacterProperty('D');
        CharacterProperty lower = new CharacterProperty('d');
        assertEquals(upper, upper);
        assertEquals(upper, upper2);
        assertNotEquals(upper, lower);
        assertNotEquals(upper, null);
        assertNotEquals(upper, new Object());
    }

    @Test void toStringWorksForProperty() {
        assertEquals("Upper", new CharacterProperty('C').toString());
        assertEquals("Upper", new CharacterProperty('D').toString());
        assertEquals("Lower", new CharacterProperty('a').toString());
        assertEquals("Lower", new CharacterProperty('e').toString());
        assertEquals("Dollar", new CharacterProperty('$').toString());
        assertEquals("Underscore", new CharacterProperty('_').toString());
        assertEquals("Digit", new CharacterProperty('2').toString());
        assertEquals("Digit", new CharacterProperty('3').toString());
    }

    @Test void canCreateFromOtherProperty() {
        CharacterProperty characterProperty = new CharacterProperty(CharacterProperty.Property.Dollar);
        assertEquals(CharacterProperty.Property.Dollar, characterProperty.property);
    }
}
