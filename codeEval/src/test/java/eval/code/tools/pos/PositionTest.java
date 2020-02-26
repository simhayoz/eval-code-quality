package eval.code.tools.pos;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test void setPosForSimplePositionWorks() {
        SinglePosition p = Position.setPos(0, 0);
        assertThat(p.line, equalTo(0));
        assertThat(p.column, equalTo(0));
        SinglePosition p2 = Position.setPos(4, 10);
        assertThat(p2.line, equalTo(4));
        assertThat(p2.column, equalTo(10));
    }

    @Test void setPosStringCorrectly() {
        assertThat(Position.setPos(0, 4).toString(), equalTo("(line 0,col 4)"));
    }

    @Test void setRangeForSimplePosWorks() {
        SinglePosition p = Position.setPos(0, 0);
        SinglePosition p2 = Position.setPos(4, 5);
        Position r = Position.setRangeOrSinglePos(p, p2);
        assertThat(r, instanceOf(Range.class));
        assertThat(((Range)r).begin.line, equalTo(0));
        assertThat(((Range)r).end.line, equalTo(4));
        assertThat(((Range)r).begin.column, equalTo(0));
        assertThat(((Range)r).end.column, equalTo(5));
    }

    @Test void setPosForSingleNegativeThrowsError() {
        assertThrows(IllegalArgumentException.class, () -> Position.setPos(2, -2));
        assertThrows(IllegalArgumentException.class, () -> Position.setPos(-5, 1));
    }

    @Test void setRangeThrowsErrorForNull() {
        SinglePosition p = Position.setPos(0, 0);
        assertThrows(NullPointerException.class, () -> {Position.setRangeOrSinglePos(p, null);});
        assertThrows(NullPointerException.class, () -> {Position.setRangeOrSinglePos(null, p);});
        assertThrows(NullPointerException.class, () -> {Position.setRangeOrSinglePos(null, null);});
    }

    @Test void setRangeReverseGivesSameRange() {
        SinglePosition p = Position.setPos(0, 0);
        SinglePosition p2 = Position.setPos(4, 5);
        assertThat(Position.setRangeOrSinglePos(p2, p), equalTo(Position.setRangeOrSinglePos(p, p2)));
    }

    @Test void setRangeForSameDifPosition() {
        SinglePosition same1 = Position.setPos(15, 4);
        SinglePosition same2 = Position.setPos(15, 4);
        SinglePosition different = Position.setPos(1, 4);
        assertThat(Position.setRangeOrSinglePos(same1, same2), instanceOf(SinglePosition.class));
        assertThat(Position.setRangeOrSinglePos(same1, same1), instanceOf(SinglePosition.class));
        assertThat(Position.setRangeOrSinglePos(same1, different), instanceOf(Range.class));
    }

}