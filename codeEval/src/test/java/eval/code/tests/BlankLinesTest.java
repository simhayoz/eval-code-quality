package eval.code.tests;

import org.junit.jupiter.api.Test;

import eval.code.tools.pos.Position;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.FileNotFoundException;
import java.util.Collection;

import org.hamcrest.Matchers;

class BlankLinesTest {

    @Test
    void fileOfOnlyBlankLineMatchFullLines() throws FileNotFoundException {
        String s = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\ntest";
        assertThat(new BlankLines(s).test(), Matchers.<Collection<Position>>allOf(
                hasItem(is(Position.setRangeOrSinglePos(Position.setPos(1, 0), Position.setPos(32, 0)))), hasSize(1)));
    }

    @Test
    void multiBlankLineTestForMultipleProblem() {
        String s = "test\n\n\nanother line\ntest\n\n\n\ntest";
        assertThat(new BlankLines(s).test(),
                Matchers.<Collection<Position>>allOf(
                        hasItems(is(Position.setRangeOrSinglePos(Position.setPos(2, 0), Position.setPos(3, 0))),
                                is(Position.setRangeOrSinglePos(Position.setPos(6, 0), Position.setPos(8, 0)))),
                        hasSize(2)));
    }

    @Test
    void emptyStringOrNoBreakStringIsSuccesful() {
        assertThat(new BlankLines("").test(), is(empty()));
        assertThat(new BlankLines("Test on a unique line").test(), is(empty()));
    }

}