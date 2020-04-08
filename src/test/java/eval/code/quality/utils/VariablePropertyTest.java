package eval.code.quality.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class VariablePropertyTest {

    @Test void stringMatchesToRightProperty() {
        assertEquals(VariableProperty.Property.AllUpper, new VariableProperty("TEST").property);
        assertEquals(VariableProperty.Property.AllUpper, new VariableProperty("tTEST_").property);
        assertEquals(VariableProperty.Property.AllUpperUnderscore, new VariableProperty("TE_ST").property);
        assertEquals(VariableProperty.Property.AllUpperUnderscore, new VariableProperty("tTE_ST$").property);
        assertEquals(VariableProperty.Property.AllUpperDollar, new VariableProperty("TE$ST").property);
        assertEquals(VariableProperty.Property.AllUpperDollar, new VariableProperty("tTE$ST_").property);

        assertEquals(VariableProperty.Property.CamelCase, new VariableProperty("thisIsATest").property);
        assertEquals(VariableProperty.Property.CamelCase, new VariableProperty("$thisIsATest_").property);

        assertEquals(VariableProperty.Property.AllLower, new VariableProperty("test").property);
        assertEquals(VariableProperty.Property.AllLower, new VariableProperty("Ttest_").property);
        assertEquals(VariableProperty.Property.AllLowerUnderscore, new VariableProperty("te_st").property);
        assertEquals(VariableProperty.Property.AllLowerUnderscore, new VariableProperty("Tte_st$").property);
        assertEquals(VariableProperty.Property.AllLowerDollar, new VariableProperty("te$st").property);
        assertEquals(VariableProperty.Property.AllLowerDollar, new VariableProperty("Tte$st_").property);

        assertEquals(VariableProperty.Property.Underscore, new VariableProperty("this_Is_A_Test").property);
        assertEquals(VariableProperty.Property.Underscore, new VariableProperty("$this_Is_A_Test$").property);
        assertEquals(VariableProperty.Property.Dollar, new VariableProperty("this$Is$A$Test").property);
        assertEquals(VariableProperty.Property.Dollar, new VariableProperty("_this$Is$A$Test_").property);

        assertEquals(VariableProperty.Property.None, new VariableProperty("this$Is$A_Test").property);
        assertEquals(VariableProperty.Property.None, new VariableProperty("_this_Is$A$Test_").property);
        assertEquals(VariableProperty.Property.Empty, new VariableProperty("__").property);
        assertEquals(VariableProperty.Property.Empty, new VariableProperty("tt").property);
        assertEquals(VariableProperty.Property.Empty, new VariableProperty("t$").property);
    }

    @Test void equalsWorksForUpperGroup() {
        VariableProperty allUpper = new VariableProperty("TEST");
        VariableProperty allUpperUnderscore = new VariableProperty("TE_ST");
        VariableProperty allUpperDollar = new VariableProperty("TE$ST");
        assertEquals(allUpper, allUpper);
        assertEquals(allUpper, allUpperUnderscore);
        assertEquals(allUpperUnderscore, allUpper);
        assertEquals(allUpper, allUpperDollar);
        assertEquals(allUpperDollar, allUpper);
        assertNotEquals(allUpperUnderscore, allUpperDollar);
        assertNotEquals(allUpperDollar, allUpperUnderscore);
    }

    @Test void equalsWorksForLowerGroup() {
        VariableProperty allLower = new VariableProperty("test");
        VariableProperty allLowerUnderscore = new VariableProperty("te_st");
        VariableProperty allLowerDollar = new VariableProperty("te$st");
        VariableProperty camelCase = new VariableProperty("thisIsATest");
        assertEquals(allLower, allLower);
        assertEquals(allLower, allLowerUnderscore);
        assertEquals(allLowerUnderscore, allLower);
        assertEquals(allLower, allLowerDollar);
        assertEquals(allLowerDollar, allLower);
        assertEquals(allLower, camelCase);
        assertEquals(camelCase, allLower);
        assertNotEquals(allLowerUnderscore, allLowerDollar);
        assertNotEquals(camelCase, allLowerUnderscore);
        assertNotEquals(camelCase, allLowerDollar);
    }

    @Test void equalsWorksForEmpty() {
        VariableProperty empty = new VariableProperty("__");
        VariableProperty notEmpty = new VariableProperty("testForLonger");
        assertEquals(empty, notEmpty);
        assertEquals(notEmpty, empty);
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
}
