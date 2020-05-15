package eval.code.quality.utils.naming;

import eval.code.quality.utils.naming.VariableProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class VariablePropertyTest {

    @Test void stringMatchesToRightProperty() {
        assertEquals(VariableProperty.Property.AllUpper, new VariableProperty("TEST").property);
        assertEquals(VariableProperty.Property.AllUpperUnderscore, new VariableProperty("TE_ST").property);
        assertEquals(VariableProperty.Property.AllUpperDollar, new VariableProperty("TE$ST").property);

        assertEquals(VariableProperty.Property.CamelCase, new VariableProperty("thisIsATest").property);

        assertEquals(VariableProperty.Property.AllLower, new VariableProperty("test").property);
        assertEquals(VariableProperty.Property.AllLowerUnderscore, new VariableProperty("te_st").property);
        assertEquals(VariableProperty.Property.AllLowerDollar, new VariableProperty("te$st").property);

        assertEquals(VariableProperty.Property.Underscore, new VariableProperty("this_Is_A_Test").property);
        assertEquals(VariableProperty.Property.Dollar, new VariableProperty("this$Is$A$Test").property);

        assertEquals(VariableProperty.Property.None, new VariableProperty("this$Is$A_Test").property);
        assertEquals(VariableProperty.Property.Empty, new VariableProperty("").property);
    }

    @Test void equalsWorksForGeneralCase() {
        VariableProperty underscore = new VariableProperty("this_Is_A_Test");
        VariableProperty underscore2 = new VariableProperty("this_Is_A_Test");
        assertEquals(underscore, underscore2);
        VariableProperty dollar = new VariableProperty("this$Is$A$Test");
        VariableProperty dollar2 = new VariableProperty("this$Is$A$Test");
        assertEquals(dollar, dollar2);
        VariableProperty none = new VariableProperty("this$Is$A_Test");
        VariableProperty none2 = new VariableProperty("this$__Is$A_Test");
        assertEquals(none, none);
        assertEquals(none, none2);
        assertNotEquals(underscore, none);
        assertNotEquals(underscore, dollar);
        assertNotEquals(none, dollar);
        assertNotEquals(underscore, null);
        assertNotEquals(underscore, new Object());
    }

    @Test void toStringWorksForProperties() {
        assertEquals("AllUpper", new VariableProperty("TEST").toString());
        assertEquals("AllUpperUnderscore", new VariableProperty("TE_ST").toString());
        assertEquals("AllUpperDollar", new VariableProperty("TE$ST").toString());

        assertEquals("CamelCase", new VariableProperty("thisIsATest").toString());

        assertEquals("AllLower", new VariableProperty("test").toString());
        assertEquals("AllLowerUnderscore", new VariableProperty("te_st").toString());
        assertEquals("AllLowerDollar", new VariableProperty("te$st").toString());

        assertEquals("Underscore", new VariableProperty("this_Is_A_Test").toString());
        assertEquals("Dollar", new VariableProperty("this$Is$A$Test").toString());

        assertEquals("None", new VariableProperty("this$Is$A_Test").toString());
    }

    @Test void canCreateFromProperty() {
        VariableProperty variableProperty = new VariableProperty(VariableProperty.Property.AllLower);
        assertEquals(VariableProperty.Property.AllLower, variableProperty.property);
    }
}
