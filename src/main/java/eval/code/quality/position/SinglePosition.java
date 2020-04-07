package eval.code.quality.position;

import eval.code.quality.utils.Preconditions;

import java.util.Optional;

/**
 * Represents a position inside a file (line,col).
 */
public class SinglePosition extends Position implements Comparable<SinglePosition> {
    public final int line;
    public final Optional<Integer> column;

    /**
     * Create a new {@code SinglePosition} from a line.
     *
     * @param line the line position
     */
    public SinglePosition(int line) {
        Preconditions.checkArg(line >= 0, "Line cannot be negative");
        this.line = line;
        this.column = Optional.empty();
    }

    /**
     * Create a new {@code SinglePosition} from a line and a column.
     *
     * @param line   the line position
     * @param column the column position
     */
    public SinglePosition(int line, int column) {
        Preconditions.checkArg(line >= 0, "Line cannot be negative");
        Preconditions.checkArg(column >= 0, "Column cannot be negative");
        this.line = line;
        this.column = Optional.of(column);
    }

    /**
     * Create a new {@code SinglePosition} from a {@code JavaParser.Position}.
     *
     * @param position the {@code com.github.javaparser.Position}
     * @return the new singlePosition
     */
    public static SinglePosition from(com.github.javaparser.Position position) {
        return new SinglePosition(position.line, position.column);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof SinglePosition)) {
            return false;
        } else {
            SinglePosition s = (SinglePosition) obj;
            if (s.line == this.line) {
                return s.column.equals(this.column);
            }
            return false;
        }
    }

    @Override
    public String toString() {
        return "(line " + line + (column.map(integer -> ",col " + integer).orElse("")) + ")";
    }

    @Override
    public int compareTo(SinglePosition p) {
        if (this.line == p.line) {
            if (this.column.isPresent() && p.column.isPresent()) {
                return Integer.compare(this.column.get(), p.column.get());
            }
            return 0;
        }
        return Integer.compare(this.line, p.line);
    }
}
