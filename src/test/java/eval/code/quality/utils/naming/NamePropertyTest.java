package eval.code.quality.utils.naming;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.*;

import eval.code.quality.utils.naming.CharacterProperty;
import eval.code.quality.utils.naming.NameProperty;
import eval.code.quality.utils.naming.VariableProperty;
import org.junit.jupiter.api.Test;

class NamePropertyTest {

    @Test void emptyOrNullStringThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new NameProperty(""));
        assertThrows(IllegalArgumentException.class, () -> new NameProperty(null));
    }

    @Test void singleOrTwoCharactersVariableThrowsNoError() {
        NameProperty nameProperty = new NameProperty("i");
        assertEquals(CharacterProperty.Property.Lower, nameProperty.startProperty.property);
        assertTrue(nameProperty.endProperty.isOther());
        assertEquals(VariableProperty.Property.Empty, nameProperty.fullProperty.property);
        NameProperty nameProperty2 = new NameProperty("_2");
        assertEquals(CharacterProperty.Property.Underscore, nameProperty2.startProperty.property);
        assertTrue(nameProperty2.endProperty.isOther());
        assertEquals(VariableProperty.Property.Digit, nameProperty2.fullProperty.property);
        NameProperty nameProperty3 = new NameProperty("__");
        assertEquals(CharacterProperty.Property.Underscore, nameProperty3.startProperty.property);
        assertEquals(CharacterProperty.Property.Underscore, nameProperty3.endProperty.property);
        assertEquals(VariableProperty.Property.Empty, nameProperty3.fullProperty.property);
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

    @Test void canCreateFromOtherProperty() {
        NameProperty nameProperty = new NameProperty(new VariableProperty(VariableProperty.Property.AllUpper),
                new NameProperty("$test1"));
        assertEquals(VariableProperty.Property.AllUpper, nameProperty.fullProperty.property);
        assertEquals(CharacterProperty.Property.Dollar, nameProperty.startProperty.property);
        assertTrue(nameProperty.endProperty.isOther());
    }
}
