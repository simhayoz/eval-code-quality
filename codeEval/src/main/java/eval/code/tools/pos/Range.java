package eval.code.tools.pos;

/**
 * Represents a range inside a file
 * <p>
 * (line1,col1) -> (line2,col2)
 * 
 * @author Simon Hayoz
 */
public class Range extends Position {
    public final SinglePosition begin;
    public final SinglePosition end;

    protected Range(SinglePosition begin, SinglePosition end) {
        if (begin == null || end == null) {
            throw new NullPointerException("Begin or end position is null");
        }
        if (begin.compareTo(end) > 0) {
            throw new IllegalArgumentException("Begin position is after end position");
        }
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