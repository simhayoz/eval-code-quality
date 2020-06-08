package eval.code.quality.block;

import com.github.javaparser.ast.stmt.Statement;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;

import java.util.List;

/**
 * Represents a child block, i.e. if else(...), catch(...) or while(); part of a statement.
 */
public class ChildBlock {

    public final SinglePosition parent;
    public final Range bracesPosition;
    public final List<Statement> childStatements;

    /**
     * Create a new {@code ChildBlock}.
     *
     * @param parent          parent of the child block
     * @param bracesPosition  braces position after this child block (or null if child block does not have braces)
     * @param childStatements list of child statements inside this child block
     */
    public ChildBlock(SinglePosition parent, Range bracesPosition, List<Statement> childStatements) {
        this.parent = parent;
        this.bracesPosition = bracesPosition;
        this.childStatements = childStatements;
    }

    @Override
    public String toString() {
        return "ChildBlock{\n" +
                ("parent=" + parent + System.lineSeparator() +
                        ("bracesPosition=" + bracesPosition).indent(2) + System.lineSeparator() +
                        ("childStatements=" + childStatements).indent(2)).indent(2) + System.lineSeparator() +
                '}';
    }
}
