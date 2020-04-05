package eval.code.quality.utils;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NamePropertyTest {

    @Test
    void emptyOrNullStringThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            NameProperty.getFor("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            NameProperty.getFor(null);
        });
    }

    @Test
    void lowerCaseProduceLowerCase() {
        NameProperty n = NameProperty.getFor("testalllower");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.AllLower));
        assertThat(n.start_property, equalTo(NameProperty.PProperty.Lower));
        assertThat(n.end_property, equalTo(NameProperty.PProperty.Lower));
        n = NameProperty.getFor("_testalllower");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.AllLower));
        assertThat(n.start_property, equalTo(NameProperty.PProperty.Underscore));
        assertThat(n.end_property, equalTo(NameProperty.PProperty.Lower));
        n = NameProperty.getFor("testalllower_");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.AllLower));
        assertThat(n.start_property, equalTo(NameProperty.PProperty.Lower));
        assertThat(n.end_property, equalTo(NameProperty.PProperty.Underscore));
        n = NameProperty.getFor("_testalllower_");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.AllLower));
        assertThat(n.start_property, equalTo(NameProperty.PProperty.Underscore));
        assertThat(n.end_property, equalTo(NameProperty.PProperty.Underscore));
    }

    @Test
    void upperCaseProduceUpperCase() {
        NameProperty n = NameProperty.getFor("TESTALLUPPER");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.AllUpper));
        assertThat(n.start_property, equalTo(NameProperty.PProperty.Upper));
        assertThat(n.end_property, equalTo(NameProperty.PProperty.Upper));
        n = NameProperty.getFor("_TESTALLUPPER");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.AllUpper));
        assertThat(n.start_property, equalTo(NameProperty.PProperty.Underscore));
        assertThat(n.end_property, equalTo(NameProperty.PProperty.Upper));
        n = NameProperty.getFor("TESTALLUPPER_");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.AllUpper));
        assertThat(n.start_property, equalTo(NameProperty.PProperty.Upper));
        assertThat(n.end_property, equalTo(NameProperty.PProperty.Underscore));
        n = NameProperty.getFor("_TESTALLUPPER_");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.AllUpper));
        assertThat(n.start_property, equalTo(NameProperty.PProperty.Underscore));
        assertThat(n.end_property, equalTo(NameProperty.PProperty.Underscore));
    }

    @Test
    void camelCaseProduceCameCase() {
        NameProperty n = NameProperty.getFor("thisIsCamelCase");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.CamelCase));
        n = NameProperty.getFor("_thisIsCamelCase");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.CamelCase));
        n = NameProperty.getFor("thisIsCamelCase_");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.CamelCase));
        n = NameProperty.getFor("_thisIsCamelCase_");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.CamelCase));
    }

    @Test
    void underscoreProduceUnderscore() {
        NameProperty n = NameProperty.getFor("this_is_underscore");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.AllLowerUnderscore));
        n = NameProperty.getFor("THIS_IS_UNDERSCORE");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.AllUpperUnderscore));
        n = NameProperty.getFor("This_Is_Underscore");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.Underscore));
    }

    @Test
    void multipleUpperProduceNone() {
        NameProperty n = NameProperty.getFor("thisIsNOTCamelCase");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.None));
    }

    @Test
    void smallerThan2ProduceNone() {
        NameProperty n = NameProperty.getFor("aa");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.None));
        n = NameProperty.getFor("_");
        assertThat(n.full_property, equalTo(NameProperty.FProperty.None));
    }

    @Test
    void equalityForSameProperty() {
        NameProperty n1 = NameProperty.getFor("_camelCaseTest");
        NameProperty n2 = NameProperty.getFor("_otherCamelCaseTestEvenLonger");
        NameProperty n3 = NameProperty.getFor("otherCamelCaseTestEvenLonger");
        NameProperty n4 = NameProperty.getFor("_otherCamelCaseTestEvenLonger_");
        NameProperty n5 = NameProperty.getFor("_this_is_another_test");
        assertFalse(n1.equals(new Object()));
        assertThat(n1, equalTo(n1));
        assertThat(n1, equalTo(n2));
        assertThat(n1, not(equalTo(n3)));
        assertThat(n1, not(equalTo(n4)));
        assertThat(n1, not(equalTo(n5)));
    }

    @Test
    void toStringWorkForEasyCase() {
        NameProperty n = NameProperty.getFor("_camelCaseTest");
        assertThat(n.toString(), equalTo("{start:" + NameProperty.PProperty.Underscore + ",end:"
                + NameProperty.PProperty.Lower + ",property:" + NameProperty.FProperty.CamelCase + "}"));
    }
}
