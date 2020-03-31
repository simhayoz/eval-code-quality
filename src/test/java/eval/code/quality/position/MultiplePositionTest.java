package eval.code.quality.position;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MultiplePositionTest {
    @Test void multiplePositionWorkForSimplePosition() {
        MultiplePosition m = new MultiplePosition();
        assertThat(m.getPositions(), is(empty()));
        m.add(new SinglePosition(0, 0));
        assertThat(m.getPositions(), Matchers.<Collection<Position>>allOf(hasItem(new SinglePosition(0, 0)), hasSize(1)));
    }

    @Test void toStringWorks() {
        MultiplePosition m = new MultiplePosition();
        m.add(new SinglePosition(0, 0));
        m.add(new SinglePosition(2, 3));
        assertThat(m.toString(), equalTo("[(line 0,col 0), (line 2,col 3)]"));
    }
}
