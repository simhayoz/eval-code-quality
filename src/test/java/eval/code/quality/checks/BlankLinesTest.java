package eval.code.quality.checks;

import eval.code.quality.MyStringBuilder;
import eval.code.quality.TestUtils;
import eval.code.quality.position.NamePosition;
import eval.code.quality.position.Range;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.StringProvider;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.hamcrest.MatcherAssert.assertThat;

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
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), new NamePosition("oneLiner", new Range(1, 32)));
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
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), new NamePosition("For tests", new Range(2, 3)), new NamePosition("For tests", new Range(6, 8)));
    }

    @Test
    void emptyStringOrNoBreakStringIsSuccessful() {
        ContentProvider contentProvider = new StringProvider("EmptyString", "");
        Report r = new BlankLines(contentProvider).run();
        TestUtils.checkIsEmptyReport(r);
        contentProvider = new StringProvider("For tests", "Test on a unique line");
        r = new BlankLines(contentProvider).run();
        TestUtils.checkIsEmptyReport(r);
    }
}
