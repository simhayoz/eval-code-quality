package eval.code.quality.position;

import eval.code.quality.utils.Preconditions;

/**
 * Represents a range inside a file (line1,col1) -> (line2,col2).
 */
public class Range extends Position {
    public final SinglePosition begin;
    public final SinglePosition end;

    public Range(int startLine, int endLine) {
        this(new SinglePosition(startLine), new SinglePosition(endLine));
    }

    public Range(int startLine, int startColumn, int endLine, int endColumn) {
        this(new SinglePosition(startLine, startColumn), new SinglePosition(endLine, endColumn));
    }

    public Range(SinglePosition begin, SinglePosition end) {
        Preconditions.checkArg(begin != null, "Begin position is null");
        Preconditions.checkArg(end != null, "End position is null");
        Preconditions.checkArg(begin.compareTo(end) <= 0, "Begin position is after end position");
        this.begin = begin;
        this.end = end;
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
