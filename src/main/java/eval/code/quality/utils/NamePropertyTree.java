package eval.code.quality.utils;

import java.util.ArrayList;
import java.util.List;

public class NamePropertyTree {

    public final Node<NameProperty> root;

    private NamePropertyTree(NameProperty nameProperty) {
        root = new Node<>(null, new NameProperty(new VariableProperty(VariableProperty.Property.Empty), nameProperty.start_property, nameProperty.end_property));
        List<Node<NameProperty>> list = new ArrayList<>();
        list.add(new Node<>(root, new NameProperty(new VariableProperty(VariableProperty.Property.None), nameProperty.start_property, nameProperty.end_property)));
        list.add(new Node<>(root, new NameProperty(new VariableProperty(VariableProperty.Property.Underscore), nameProperty.start_property, nameProperty.end_property)));
        list.add(new Node<>(root, new NameProperty(new VariableProperty(VariableProperty.Property.Dollar), nameProperty.start_property, nameProperty.end_property)));
        Node<NameProperty> allUpper = new Node<>(root, new NameProperty(new VariableProperty(VariableProperty.Property.AllUpper), nameProperty.start_property, nameProperty.end_property));
        list.add(allUpper);
        Node<NameProperty> allLower = new Node<>(root, new NameProperty(new VariableProperty(VariableProperty.Property.AllLower), nameProperty.start_property, nameProperty.end_property));
        list.add(allLower);
        root.setChildren(list);
        List<Node<NameProperty>> listUpper = new ArrayList<>();
        listUpper.add(new Node<>(allUpper, new NameProperty(new VariableProperty(VariableProperty.Property.AllUpperUnderscore), nameProperty.start_property, nameProperty.end_property)));
        listUpper.add(new Node<>(allUpper, new NameProperty(new VariableProperty(VariableProperty.Property.AllUpperDollar), nameProperty.start_property, nameProperty.end_property)));
        allUpper.setChildren(listUpper);
        List<Node<NameProperty>> listLower = new ArrayList<>();
        listLower.add(new Node<>(allLower, new NameProperty(new VariableProperty(VariableProperty.Property.AllLowerUnderscore), nameProperty.start_property, nameProperty.end_property)));
        listLower.add(new Node<>(allLower, new NameProperty(new VariableProperty(VariableProperty.Property.AllLowerDollar), nameProperty.start_property, nameProperty.end_property)));
        listLower.add(new Node<>(allLower, new NameProperty(new VariableProperty(VariableProperty.Property.CamelCase), nameProperty.start_property, nameProperty.end_property)));
        allLower.setChildren(listLower);
    }

    public static Node<NameProperty> getCurrentNodeForTree(NameProperty nameProperty) {
        return new NamePropertyTree(nameProperty).root.getChildrenWithValueOrNull(nameProperty);
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
