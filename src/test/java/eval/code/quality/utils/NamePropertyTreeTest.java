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
        NameProperty nameProperty = new NameProperty("i");
        Node<NameProperty> node = NamePropertyTree.getCurrentNodeForTree(nameProperty);
        assertThat(node.toString(), equalTo("{start:Lower,end:Lower,property:Empty}" +
                "{{start:Lower,end:Lower,property:None}, {start:Lower,end:Lower,property:Underscore}, " +
                "{start:Lower,end:Lower,property:Dollar}, {start:Lower,end:Lower,property:AllUpper}" +
                "{{start:Lower,end:Lower,property:AllUpperUnderscore}, {start:Lower,end:Lower,property:AllUpperDollar}}, " +
                "{start:Lower,end:Lower,property:AllLower}{{start:Lower,end:Lower,property:AllLowerUnderscore}, " +
                "{start:Lower,end:Lower,property:AllLowerDollar}, {start:Lower,end:Lower,property:CamelCase}}}"));
    }
}
