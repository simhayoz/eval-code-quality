package eval.code.tools.pos;

import java.util.ArrayList;
import java.util.List;

public class PositionList extends Position {

    private final List<Position> positions;

    public PositionList(List<Position> positions) {
        this.positions = positions;
    }

    public static PositionList empty() {
        return new PositionList(new ArrayList<>());
    }

    public boolean add(Position pos) {
        return positions.add(pos);
    }

    @Override
    public String toString() {
        return positions.toString();
    }
}