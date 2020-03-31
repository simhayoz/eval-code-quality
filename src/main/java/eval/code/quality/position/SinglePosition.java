package eval.code.quality.position;

import eval.code.quality.utils.Preconditions;

import java.util.Optional;

/**
 * Represents a position inside a file (line,col).
 */
public class SinglePosition extends Position implements Comparable<SinglePosition> {
    public final int line;
    public final Optional<Integer> column;

    public SinglePosition(int line) {
        Preconditions.checkArg(line >= 0, "Line cannot be negative");
        this.line = line;
        this.column = Optional.empty();
    }

    public SinglePosition(int line, int column) {
        Preconditions.checkArg(line >= 0, "Line cannot be negative");
        Preconditions.checkArg(column >= 0, "Column cannot be negative");
        this.line = line;
        this.column = Optional.of(column);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof SinglePosition)) {
            return false;
        } else {
            SinglePosition s = (SinglePosition) obj;
            if(s.line == this.line) {
                return (!s.column.isPresent() && !this.column.isPresent()) || s.column.get() == this.column.get();
            }
            return false;
        }
    }

    @Override
    public String toString() {
        return "(line " + line + (column.isPresent() ? ",col " + column.get() : "") + ")";
    }

    @Override
    public int compareTo(SinglePosition p) {
        if (this.line == p.line) {
            if(this.column.isPresent() && p.column.isPresent()) {
                return Integer.compare(this.column.get(), p.column.get());
            }
            return 0;
        }
        return Integer.compare(this.line, p.line);
    }
}
