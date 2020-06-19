package eval.code.quality.position;

import eval.code.quality.utils.Preconditions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Objects;

/**
 * Represents a range inside a file (line1,col1) -> (line2,col2).
 */
public class Range extends Position {
    public final SinglePosition begin;
    public final SinglePosition end;

    /**
     * Create a new {@code Range} from a start line and end line.
     *
     * @param startLine the start line
     * @param endLine   the end line
     */
    public Range(int startLine, int endLine) {
        this(new SinglePosition(startLine), new SinglePosition(endLine));
    }

    /**
     * Create a new {@code Range} from a start position and end position.
     *
     * @param startLine   the start line
     * @param startColumn the start column
     * @param endLine     the end line
     * @param endColumn   the end column
     */
    public Range(int startLine, int startColumn, int endLine, int endColumn) {
        this(new SinglePosition(startLine, startColumn), new SinglePosition(endLine, endColumn));
    }

    /**
     * Create a new {@code Range} from a start position and end position.
     *
     * @param begin the start position
     * @param end   the end position
     */
    public Range(SinglePosition begin, SinglePosition end) {
        Preconditions.checkArg(begin != null, "Begin position is null");
        Preconditions.checkArg(end != null, "End position is null");
        Preconditions.checkArg(begin.compareTo(end) <= 0, "Begin position is after end position");
        this.begin = begin;
        this.end = end;
    }

    /**
     * Create a new {@code Range} from a {@code JavaParser.Range}.
     *
     * @param range the {@code com.github.javaparser.Range}
     * @return the new range
     */
    public static Range from(com.github.javaparser.Range range) {
        Preconditions.checkArg(range != null, "The range cannot be null");
        return new Range(SinglePosition.from(range.begin), SinglePosition.from(range.end));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Range that = (Range) o;
        return this.begin.equals(that.begin) && this.end.equals(that.end);
    }

    @Override
    public int hashCode() {
        return 89 * Objects.hashCode(begin) + Objects.hashCode(end);
    }

    @Override
    public String toString() {
        return "(" + begin + " -> " + end + ")";
    }

    @Override
    public Element getXMLElement(Document document) {
        Element range = document.createElement("range");
        Element beginNode = document.createElement("begin");
        Element endNode = document.createElement("end");
        beginNode.setAttribute("line", Integer.toString(begin.line));
        begin.column.ifPresent(col -> beginNode.setAttribute("col", Integer.toString(col)));
        endNode.setAttribute("line", Integer.toString(end.line));
        end.column.ifPresent(col -> endNode.setAttribute("col", Integer.toString(col)));
        range.appendChild(beginNode);
        range.appendChild(endNode);
        return range;
    }

}
