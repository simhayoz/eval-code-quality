package eval.code.quality.position;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class SinglePositionTest {
    @Test void setPosForSimplePositionWorks() {
        SinglePosition p = new SinglePosition(0, 0);
        assertThat(p.line, equalTo(0));
        assertThat(p.column.get(), equalTo(0));
        SinglePosition p2 = new SinglePosition(4, 10);
        assertThat(p2.line, equalTo(4));
        assertThat(p2.column.get(), equalTo(10));
    }

    @Test void setPosStringCorrectly() {
        assertThat(new SinglePosition(0, 4).toString(), equalTo("(line 0,col 4)"));
        assertThat(new SinglePosition(3).toString(), equalTo("(line 3)"));
    }

    @Test void setPosForSingleNegativeThrowsError() {
        assertThrows(IllegalArgumentException.class, () -> new SinglePosition(2, -2));
        assertThrows(IllegalArgumentException.class, () -> new SinglePosition(-5, 1));
        assertThrows(IllegalArgumentException.class, () -> new SinglePosition(-5));
    }

    @Test void sameSinglePositionAreEqual() {
        SinglePosition p = new SinglePosition(1, 0);
        SinglePosition p2 = new SinglePosition(1, 0);
        assertTrue(p.equals(p2));
        p = new SinglePosition(3);
        p2 = new SinglePosition(3);
        assertTrue(p.equals(p2));
        assertTrue(p.equals(p));
        assertFalse(p.equals(new Object()));
        p = new SinglePosition(4, 0);
        p2 = new SinglePosition(5, 0);
        assertFalse(p.equals(p2));
    }

    @Test void compareToWorks() {
        SinglePosition p = new SinglePosition(1, 0);
        SinglePosition p2 = new SinglePosition(1, 0);
        assertThat(p.compareTo(p2), is(0));
        p = new SinglePosition(4);
        p2 = new SinglePosition(4);
        assertThat(p.compareTo(p2), is(0));
    }
}
