package eval.code.quality.utils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Node<T> {
    public final T value;
    private final Node<T> parent;
    private List<Node<T>> children;

    public Node(Node<T> parent, T value) {
        this.parent = parent;
        this.value = value;
        this.children = null;
    }

    public Node(Node<T> parent, T value, List<Node<T>> children) {
        this.parent = parent;
        this.value = value;
        this.children = children;
    }

    public void setChildren(List<Node<T>> children) {
        this.children = children;
    }

    public Node<T> getChildrenWithValueOrNull(T val) {
        if(children == null) {
            return null;
        }
        return children.stream().filter(e -> e.value.equals(val)).findFirst().orElse(children.stream().map(e -> e.getChildrenWithValueOrNull(val)).filter(Objects::nonNull).findFirst().orElse(null));
    }

    public boolean hasParent(T val) {
        if(parent == null) {
            return false;
        } else if(parent.value.equals(val)) {
            return true;
        } else {
            return parent.hasParent(val);
        }
    }

    @Override
    public String toString() {
        if(children != null) {
            return value + "{" + children.stream().map(Node::toString).collect(Collectors.joining(", ")) + "}";
        } else {
            return value.toString();
        }
    }
}
