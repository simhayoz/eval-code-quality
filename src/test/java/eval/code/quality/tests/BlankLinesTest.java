package eval.code.quality.tests;

import eval.code.quality.MyStringBuilder;
import eval.code.quality.position.Range;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.StringProvider;
import eval.code.quality.utils.Error;
import eval.code.quality.utils.ReportPosition;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsEmptyCollection.empty;

public class BlankLinesTest {
    @Test
    void fileOfOnlyBlankLineMatchFullLines() throws FileNotFoundException {
        MyStringBuilder s = new MyStringBuilder();
        for (int i = 0; i < 32; ++i) {
            s.addBlankLine();
        }
        s.addLn("test");
        ContentProvider contentProvider = new StringProvider("oneLiner", s.toString());
        Report r = new BlankLines(contentProvider).run();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), Matchers.<Collection<Error>>allOf(
                hasItem(is(ReportPosition.at(new Range(1, 32)))), hasSize(1)));
    }

    @Test
    void multiBlankLineTestForMultipleProblem() {
        MyStringBuilder s = new MyStringBuilder();
        s.addLn("test")
                .addBlankLine()
                .addBlankLine()
                .addLn("another line")
                .addLn("test")
                .addBlankLine()
                .addBlankLine()
                .addBlankLine()
                .addLn("test");
        ContentProvider contentProvider = new StringProvider("For tests", s.toString());
        Report r = new BlankLines(contentProvider).run();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(),
                Matchers.<Collection<Error>>allOf(
                        hasItems(is(ReportPosition.at(new Range(2, 3))),
                                is(ReportPosition.at(new Range(6, 8)))),
                        hasSize(2)));
    }

    @Test
    void emptyStringOrNoBreakStringIsSuccesful() {
        ContentProvider contentProvider = new StringProvider("EmptyString", "");
        Report r = new BlankLines(contentProvider).run();
        assertThat(r.getErrors(), is(empty()));
        assertThat(r.getWarnings(), is(empty()));
        contentProvider = new StringProvider("For tests", "Test on a unique line");
        r = new BlankLines(contentProvider).run();
        assertThat(r.getErrors(), is(empty()));
        assertThat(r.getWarnings(), is(empty()));
    }
}
