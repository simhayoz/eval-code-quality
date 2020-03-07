package eval.code.tests;

import org.junit.jupiter.api.Test;

import eval.code.tools.pos.Position;
import eval.code.tools.pos.ReportPosition;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.FileNotFoundException;
import java.util.Collection;

import org.hamcrest.Matchers;

class BlankLinesTest {

    @Test
    void fileOfOnlyBlankLineMatchFullLines() throws FileNotFoundException {
        String s = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\ntest";
        Report r = new BlankLines(s).runTest();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), Matchers.<Collection<ReportPosition>>allOf(
                hasItem(is(ReportPosition.at(Position.setRangeOrSinglePos(Position.setPos(1, 0), Position.setPos(32, 0))))), hasSize(1)));
    }

    @Test
    void multiBlankLineTestForMultipleProblem() {
        String s = "test\n\n\nanother line\ntest\n\n\n\ntest";
        Report r = new BlankLines(s).runTest();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(),
                Matchers.<Collection<ReportPosition>>allOf(
                        hasItems(is(ReportPosition.at(Position.setRangeOrSinglePos(Position.setPos(2, 0), Position.setPos(3, 0)))),
                                is(ReportPosition.at(Position.setRangeOrSinglePos(Position.setPos(6, 0), Position.setPos(8, 0))))),
                        hasSize(2)));
    }

    @Test
    void emptyStringOrNoBreakStringIsSuccesful() {
        Report r = new BlankLines("").runTest();
        assertThat(r.getErrors(), is(empty()));
        assertThat(r.getWarnings(), is(empty()));
        r = new BlankLines("Test on a unique line").runTest();
        assertThat(r.getErrors(), is(empty()));
        assertThat(r.getWarnings(), is(empty()));
    }

}