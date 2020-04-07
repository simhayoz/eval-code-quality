package eval.code.quality.position;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class RangeTest {
    @Test
    void setRangeThrowsErrorForNull() {
        SinglePosition p = new SinglePosition(0, 0);
        assertThrows(IllegalArgumentException.class, () -> {new Range(p, null);});
        assertThrows(IllegalArgumentException.class, () -> {new Range(null, p);});
        assertThrows(IllegalArgumentException.class, () -> {new Range(null, null);});
        assertThrows(IllegalArgumentException.class, () -> {Range.from(null);});
    }

    @Test void setRangeNegativeOrderDoesNotWork() {
        SinglePosition p = new SinglePosition(0, 0);
        SinglePosition p2 = new SinglePosition(5, 8);
        assertThrows(IllegalArgumentException.class, () -> new Range(p2, p));
    }


    @Test void setRangeForSimplePosWorks() {
        SinglePosition p = new SinglePosition(0, 0);
        SinglePosition p2 = new SinglePosition(4, 5);
        Range r = new Range(p, p2);
        assertThat(r.begin.line, equalTo(0));
        assertThat(r.end.line, equalTo(4));
        assertThat(r.begin.column.get(), equalTo(0));
        assertThat(r.end.column.get(), equalTo(5));
    }

    @Test void canCreateFromInt() {
        Range r = new Range(1, 3);
        assertThat(r.begin.line, equalTo(1));
        assertThat(r.end.line, equalTo(3));
        assertThat(r.begin.column, equalTo(Optional.empty()));
        assertThat(r.end.column, equalTo(Optional.empty()));
        r = new Range(1, 3, 5, 7);
        assertThat(r.begin.line, equalTo(1));
        assertThat(r.end.line, equalTo(5));
        assertThat(r.begin.column.get(), equalTo(3));
        assertThat(r.end.column.get(), equalTo(7));
    }

    @Test void toStringProduceRightString() {
        Range r = new Range(1, 3);
        assertThat(r.toString(), equalTo("((line 1) -> (line 3))"));
        r = new Range(1, 3, 5, 7);
        assertThat(r.toString(), equalTo("((line 1,col 3) -> (line 5,col 7))"));
    }

    @Test void equalWorkForSimpleRange() {
        Range r = new Range(1, 3);
        Range r2 = new Range(1, 3);
        Range r3 = new Range(1, 4);
        assertTrue(r.equals(r));
        assertFalse(r.equals(new Object()));
        assertTrue(r.equals(r2));
        assertFalse(r.equals(r3));
    }

    @Test void canCreateFromJavaParserPosition() {
        com.github.javaparser.Position begin = new com.github.javaparser.Position(1, 2);
        com.github.javaparser.Position end = new com.github.javaparser.Position(3, 4);
        com.github.javaparser.Range range = new com.github.javaparser.Range(begin, end);
        Range rangeFrom = Range.from(range);
        assertThat(rangeFrom.begin, equalTo(new SinglePosition(1, 2)));
        assertThat(rangeFrom.end, equalTo(new SinglePosition(3, 4)));
    }
}
