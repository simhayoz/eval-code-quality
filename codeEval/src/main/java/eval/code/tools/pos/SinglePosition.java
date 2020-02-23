package eval.code.tools.pos;

/**
 * Represents a position inside a file <p>
 * (line,col)
 * 
 * @author Simon Hayoz
 */
public class SinglePosition extends Position implements Comparable<SinglePosition> {
    public final int line;
    public final int column;

    protected SinglePosition(int line, int column) {
        if(line < 0 || column < 0) {
            throw new IllegalArgumentException("Line or column is negative");
        }
        this.line = line;
        this.column = column;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        } else if(!(obj instanceof SinglePosition)) {
            return false;
        } else {
            SinglePosition s = (SinglePosition)obj;
            return s.line == this.line && s.column == this.column;
        }
    }

    @Override
    public String toString() {
        return "(line " + line + ",col " + column+")";
    }

    @Override
    public int compareTo(SinglePosition p) {
        if(this.line == p.line) {
            return Integer.compare(this.column, p.column);
        }
        return Integer.compare(this.line, p.line);
    }
  
  }