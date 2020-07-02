package eval.code.quality.utils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a node with multiple children node and with parent node.
 *
 * @param <T> the type of element of the node
 */
public class Node<T> {
    public final T value;
    private final Node<T> parent;
    private List<Node<T>> children;

    /**
     * Create a new {@code Node} with a parent, no children and a value.
     *
     * @param parent the parent node
     * @param value  the value of this node
     */
    public Node(Node<T> parent, T value) {
        Preconditions.checkArg(value != null, "Value of the node cannot be null");
        this.parent = parent;
        this.value = value;
        this.children = null;
    }

    /**
     * Set the children of the current node.
     *
     * @param children the children to set to the current node
     */
    public void setChildren(List<Node<T>> children) {
        this.children = children;
    }

    /**
     * Get the children of the current node that has the value {@code val} or null if there exists no such children.
     *
     * @param val the value to search in the children
     * @return either null if no children found or the node of the children having the needed value
     */
    public Node<T> getChildrenWithValueOrNull(T val) {
        if (children == null) {
            return null;
        }
        return children.stream().filter(e -> e.value.equals(val)).findFirst().orElse(children.stream().map(e -> e.getChildrenWithValueOrNull(val)).filter(Objects::nonNull).findFirst().orElse(null));
    }

    /**
     * Check whether a parent of the current node has the value {@code val}.
     *
     * @param val the value to search in the parent
     * @return whether a parent of the current node has the value {@code val}
     */
    public boolean hasParent(T val) {
        if (parent == null) {
            return false;
        } else if (parent.value.equals(val)) {
            return true;
        } else {
            return parent.hasParent(val);
        }
    }

    @Override
    public String toString() {
        if (children != null) {
            return value + "{" + children.stream().map(Node::toString).collect(Collectors.joining(", ")) + "}";
        } else {
            return value.toString();
        }
    }
}
