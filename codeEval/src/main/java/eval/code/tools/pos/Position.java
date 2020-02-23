package eval.code.tools.pos;

/**
 * Superclass for position inside a file
 * 
 * @author Simon Hayoz
 */
public abstract class Position {

    /**
     * Return a new SinglePosition
     * @param line
     * @param column
     * @return the newly created SinglePosition
     */
    public static SinglePosition setPos(int line, int column) {
        return new SinglePosition(line, column);
    }
    
    /**
     * Return either a SinglePosition if both position are the same or a Range
     * @param start
     * @param end
     * @return a Position which is either a SinglePosition or a Range
     */
    public static Position setRangeOrSinglePos(SinglePosition start, SinglePosition end) {
        if(start.equals(end)) {
            return start;
        } else {
            if(start.compareTo(end) > 0) {
                return new Range(end, start);
            } else {
                return new Range(start, end);
            }
        }
    }
}