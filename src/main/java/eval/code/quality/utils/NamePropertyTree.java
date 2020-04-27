package eval.code.quality.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the tree of naming property depending on each other.
 * This is represented by the following tree:
 *
 *                                Empty
 *                                 |
 *                                 |
 *                                Digit (special case for "_1", for example)
 *                                 |
 *                                 |
 *    +---------------------------------------------------------------------------+
 *    |         |             |               |                                   |
 *    |         |             |               |                                   |
 *    |         |             |               |                                   |
 *    |         |             |               |                                   |
 *  None   Underscore      Dollar         AllUpper                            AllLower
 *                                            |                                  |
 *                                            |                                  |
 *                                 +----------------+                  +------------------------+
 *                                 |                |                  |           |            |
 *                                 |                |                  |           |            |
 *                          AllUpperDollar  AllUpperUnderscore  AllLowerDollar  CamelCase  AllLowerUnderscore
 */
public class NamePropertyTree {

    public final Node<NameProperty> root;

    private NamePropertyTree(NameProperty nameProperty) {
        root = new Node<>(null, new NameProperty(new VariableProperty(VariableProperty.Property.Empty), nameProperty));
        Node<NameProperty> digit = new Node<>(root, new NameProperty(new VariableProperty(VariableProperty.Property.Digit), nameProperty));
        root.setChildren(Collections.singletonList(digit));
        List<Node<NameProperty>> list = new ArrayList<>();
        list.add(new Node<>(digit, new NameProperty(new VariableProperty(VariableProperty.Property.None), nameProperty)));
        list.add(new Node<>(digit, new NameProperty(new VariableProperty(VariableProperty.Property.Underscore), nameProperty)));
        list.add(new Node<>(digit, new NameProperty(new VariableProperty(VariableProperty.Property.Dollar), nameProperty)));
        Node<NameProperty> allUpper = new Node<>(digit, new NameProperty(new VariableProperty(VariableProperty.Property.AllUpper), nameProperty));
        list.add(allUpper);
        Node<NameProperty> allLower = new Node<>(digit, new NameProperty(new VariableProperty(VariableProperty.Property.AllLower), nameProperty));
        list.add(allLower);
        digit.setChildren(list);
        List<Node<NameProperty>> listUpper = new ArrayList<>();
        listUpper.add(new Node<>(allUpper, new NameProperty(new VariableProperty(VariableProperty.Property.AllUpperUnderscore), nameProperty)));
        listUpper.add(new Node<>(allUpper, new NameProperty(new VariableProperty(VariableProperty.Property.AllUpperDollar), nameProperty)));
        allUpper.setChildren(listUpper);
        List<Node<NameProperty>> listLower = new ArrayList<>();
        listLower.add(new Node<>(allLower, new NameProperty(new VariableProperty(VariableProperty.Property.AllLowerUnderscore), nameProperty)));
        listLower.add(new Node<>(allLower, new NameProperty(new VariableProperty(VariableProperty.Property.AllLowerDollar), nameProperty)));
        listLower.add(new Node<>(allLower, new NameProperty(new VariableProperty(VariableProperty.Property.CamelCase), nameProperty)));
        allLower.setChildren(listLower);
    }

    /**
     * Create a new Tree with the start and end property corresponding to the {@code nameProperty} and return the node corresponding to the {@code nameProperty}.
     *
     * @param nameProperty the property to set the tree to
     * @return the node corresponding to the {@code nameProperty}
     */
    public static Node<NameProperty> getCurrentNodeForTree(NameProperty nameProperty) {
        Preconditions.checkArg(nameProperty != null, "The name property can not be null");
        Node<NameProperty> root = new NamePropertyTree(nameProperty).root;
        if(root.value.equals(nameProperty)) {
            return root;
        }
        return root.getChildrenWithValueOrNull(nameProperty);
    }
}
