package eval.code.quality.utils;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NamePropertyTreeTest {

    @Test void nullNamePropertyThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> NamePropertyTree.getCurrentNodeForTree(null));
    }

    @Test void canGetNodeFromCreatedTree() {
        NameProperty nameProperty = new NameProperty("$TEST_");
        NameProperty namePropertyRoot = new NameProperty(new VariableProperty(VariableProperty.Property.Empty), nameProperty);
        Node<NameProperty> node = NamePropertyTree.getCurrentNodeForTree(nameProperty);
        assertThat(node.value, equalTo(nameProperty));
        assertThat(node.hasParent(namePropertyRoot), equalTo(true));
    }

    @Test void printFromNodesPrintTheRightTree() {
        NameProperty nameProperty = new NameProperty("__");
        Node<NameProperty> node = NamePropertyTree.getCurrentNodeForTree(nameProperty);
        assertThat(node.toString(), equalTo("{start:Underscore,end:Underscore,property:Empty}" +
                "{{start:Underscore,end:Underscore,property:Digit}" +
                "{{start:Underscore,end:Underscore,property:None}, {start:Underscore,end:Underscore,property:Underscore}, " +
                "{start:Underscore,end:Underscore,property:Dollar}, {start:Underscore,end:Underscore,property:AllUpper}" +
                "{{start:Underscore,end:Underscore,property:AllUpperUnderscore}, {start:Underscore,end:Underscore,property:AllUpperDollar}}, " +
                "{start:Underscore,end:Underscore,property:AllLower}{{start:Underscore,end:Underscore,property:AllLowerUnderscore}, " +
                "{start:Underscore,end:Underscore,property:AllLowerDollar}, {start:Underscore,end:Underscore,property:CamelCase}}}}"));
    }
}
