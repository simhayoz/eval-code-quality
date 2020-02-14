package Tools;

public class Range {
    public final Position begin;
    public final Position end;

    public Range(Position begin, Position end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public String toString() {
        return "(" + begin + ", " + end + ")";
    }
}