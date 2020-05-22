package eval.code.quality.block;

import com.github.javaparser.ast.stmt.Statement;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;

import java.util.List;

public class ChildBlock {

    public final SinglePosition parent;
    public final Range bracesPosition;
    public final List<Statement> childStatements;

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
