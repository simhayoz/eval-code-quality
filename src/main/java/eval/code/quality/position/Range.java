package eval.code.quality.position;

import eval.code.quality.utils.Preconditions;

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
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof Range)) {
            return false;
        } else {
            Range r = (Range) obj;
            return this.begin.equals(r.begin) && this.end.equals(r.end);
        }
    }

    @Override
    public String toString() {
        return "(" + begin + " -> " + end + ")";
    }
}
