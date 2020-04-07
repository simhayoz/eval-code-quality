package eval.code.quality.position;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MultiplePositionTest {

    @Test void nullListThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new MultiplePosition(null));
    }

    @Test void multiplePositionWorkForSimplePosition() {
        MultiplePosition m = new MultiplePosition();
        assertThat(m.getPositions(), is(empty()));
        m.add(new SinglePosition(0, 0));
        assertThat(m.getPositions(), Matchers.<Collection<Position>>allOf(hasItem(new SinglePosition(0, 0)), hasSize(1)));
        List<Position> list = new ArrayList<>();
        list.add(new SinglePosition(1, 2));
        list.add(new SinglePosition(3, 4));
        MultiplePosition multiplePosition = new MultiplePosition(list);
        assertThat(multiplePosition.getPositions(), Matchers.<Collection<Position>>allOf(hasItem(new SinglePosition(1, 2)),
                hasItem(new SinglePosition(3, 4)), hasSize(2)));
    }

    @Test void toStringWorks() {
        MultiplePosition m = new MultiplePosition();
        m.add(new SinglePosition(0, 0));
        m.add(new SinglePosition(2, 3));
        assertThat(m.toString(), equalTo("[(line 0,col 0), (line 2,col 3)]"));
    }
}
