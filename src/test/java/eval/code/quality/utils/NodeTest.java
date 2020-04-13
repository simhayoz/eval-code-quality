package eval.code.quality.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class NodeTest {

    @Test void nullValueThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new Node<String>(null, null));
    }

    @Test void canCreateSimpleTree() {
        Node<String> root = getSimpleTree();
        assertNotNull(root.getChildrenWithValueOrNull("children24"));
        assertNotNull(root.getChildrenWithValueOrNull("children1"));
        assertNull(root.getChildrenWithValueOrNull("children"));
        assertTrue(root.getChildrenWithValueOrNull("children24").hasParent("root"));
        assertTrue(root.getChildrenWithValueOrNull("children24").hasParent("children2"));
        assertFalse(root.getChildrenWithValueOrNull("children24").hasParent("children1"));
    }

    @Test void toStringWorksForSimpleTree() {
        Node<String> root = getSimpleTree();
        assertThat(root.toString(), equalTo("root{children1, children2{children21, children22, children23, children24}, children3}"));
    }

    private Node<String> getSimpleTree() {
        Node<String> root = new Node<>(null, "root");
        List<Node<String>> list = new ArrayList<>();
        list.add(new Node<>(root, "children1"));
        Node<String> children2 = new Node<>(root, "children2");
        list.add(children2);
        list.add(new Node<>(root, "children3"));
        root.setChildren(list);
        List<Node<String>> children2List = new ArrayList<>();
        children2List.add(new Node<>(children2, "children21"));
        children2List.add(new Node<>(children2, "children22"));
        children2List.add(new Node<>(children2, "children23"));
        children2List.add(new Node<>(children2, "children24"));
        children2.setChildren(children2List);
        return root;
    }
}
