package eval.code.quality.utils;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NamePropertyTest {

    @Test void emptyOrNullStringThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new NameProperty(""));
        assertThrows(IllegalArgumentException.class, () -> new NameProperty(null));
    }

    @Test void singleOrTwoCharactersVariableThrowsNoError() {
        NameProperty nameProperty = new NameProperty("i");
        assertEquals(CharacterProperty.Property.Lower, nameProperty.start_property.property);
        assertEquals(CharacterProperty.Property.Lower, nameProperty.end_property.property);
        assertEquals(VariableProperty.Property.Empty, nameProperty.full_property.property);
        NameProperty nameProperty2 = new NameProperty("i2");
        assertEquals(CharacterProperty.Property.Lower, nameProperty2.start_property.property);
        assertEquals(CharacterProperty.Property.Digit, nameProperty2.end_property.property);
        assertEquals(VariableProperty.Property.Empty, nameProperty2.full_property.property);
    }

    @Test void equalityWorksForSimpleVariable() {
        NameProperty nameProperty = new NameProperty("_testForCamelCase$");
        NameProperty nameProperty2 = new NameProperty("_testForSecondDifferent$");
        NameProperty nameProperty3 = new NameProperty("testForThirdDifferent$");
        assertEquals(nameProperty, nameProperty);
        assertEquals(nameProperty, nameProperty2);
        assertNotEquals(nameProperty, nameProperty3);
        assertNotEquals(nameProperty2, nameProperty3);
        assertNotEquals(nameProperty, null);
        assertNotEquals(nameProperty, new Object());
        nameProperty3 = new NameProperty("test_For_Different$");
        assertNotEquals(nameProperty, nameProperty3);
        nameProperty2 = new NameProperty("_testForSecondDifferent_");
        assertNotEquals(nameProperty, nameProperty2);
    }

    @Test void toStringWorksForSomeProperties() {
        NameProperty nameProperty = new NameProperty("_testForCamelCase$");
        assertEquals("{start:Underscore,end:Dollar,property:CamelCase}", nameProperty.toString());
    }
}
