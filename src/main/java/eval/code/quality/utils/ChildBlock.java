package eval.code.quality.utils;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.Statement;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;

import java.util.List;

public class ChildBlock {

    public final SinglePosition parent;
    public final Range bracketPosition;
    public final List<Statement> childStatements;

    public ChildBlock(SinglePosition parent, Range bracketPosition, List<Statement> childStatements) {
        this.parent = parent;
        this.bracketPosition = bracketPosition;
        this.childStatements = childStatements;
    }


    @Override
    public String toString() {
        return "ChildBlock{\n" +
                ("parent=" + parent + System.lineSeparator() +
                        ("bracketPosition=" + bracketPosition).indent(2) + System.lineSeparator() +
                                ("childStatements=" + childStatements).indent(2)).indent(2) + System.lineSeparator() +
                '}';
    }
}
