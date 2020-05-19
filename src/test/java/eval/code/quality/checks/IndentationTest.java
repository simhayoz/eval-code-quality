package eval.code.quality.checks;

import eval.code.quality.MyStringBuilder;
import eval.code.quality.TestUtils;
import eval.code.quality.position.*;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.StringProvider;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.Map.Entry;

class IndentationTest {

    @Test
    void emptyCUReportNoError() {
        ContentProvider contentProvider = new StringProvider("Empty String", "");
        TestUtils.checkIsEmptyReport(new Indentation(contentProvider).run());
    }

    @Test void typeOrImportNotAlignedLeftFails() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("import java.utils.List;")
                .addLn("import java.utils.ArrayList;", 1)
                .addBlankLine()
                .addLn("public class Test {", 1)
                .addBlankLine()
                .addLn("}");
        Report r = new Indentation(new StringProvider("For tests", builder.toString())).run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), new NamePosition("For tests",
                new SinglePosition(2, 2)),
                new NamePosition("For tests", new SinglePosition(4, 2)));
    }

    @Test
    void testForSimpleWhileForIfWorks() {
        MyStringBuilder builder = new MyStringBuilder();
        String[] blocks_to_test = new String[5];
        builder.addLn("for(int i=0; i < 2; ++i) {")
                .addLn("return true;", 4)
                .addLn("}");
        blocks_to_test[0] = builder.toString();
        builder = new MyStringBuilder();
        builder.addLn("if(true) {")
                .addLn("return true;", 4)
                .addLn("} else {")
                .addLn("return false;", 4)
                .addLn("}")
                .addLn("if(true)")
                .addLn("return true;", 4);
        blocks_to_test[1] = builder.toString();
        builder = new MyStringBuilder();
        builder.addLn("while(true) {")
                .addLn("System.out.println();", 4)
                .addLn("return true;", 4)
                .addLn("}");
        blocks_to_test[2] = builder.toString();
        builder = new MyStringBuilder();
        builder.addLn("{")
                .addLn("System.out.println();", 4)
                .addLn("return true;", 4)
                .addLn("}");
        blocks_to_test[3] = builder.toString();
        builder = new MyStringBuilder();
        builder.addLn("int i = 0;")
                .addLn("switch (i) {")
                .addLn("case 0:", 4)
                .addLn("return true;", 8)
                .addLn("break;", 8)
                .addLn("case 1:", 4)
                .addLn("return false;", 8)
                .addLn("break;", 8)
                .addLn("default:", 4)
                .addLn("return false;", 8)
                .addLn("break;", 8)
                .addLn("}");
        blocks_to_test[4] = builder.toString();
        for (String s : blocks_to_test) {
            String wrapper = wrap(s);
            TestUtils.checkIsEmptyReport(new Indentation(new StringProvider("For tests", wrapper)).run());
        }
    }

    @Test
    void testForSimpleWhileForIfFailsWhenMisaligned() {
        Map<String, Position> blocks_to_test = new HashMap<>();
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("for(int i=0; i < 2; ++i) {")
                .addLn("return true;", 2)
                .addLn("}");
        blocks_to_test.put(builder.toString(), new NamePosition("For tests", new SinglePosition(4, 11)));
        builder = new MyStringBuilder();
        builder.addLn("if(true) {")
                .addLn("return true;", 4)
                .addLn("} else {")
                .addLn("return false;", 5)
                .addLn("}");
        blocks_to_test.put(builder.toString(), new NamePosition("For tests", new SinglePosition(6, 14)));
        builder = new MyStringBuilder();
        builder.addLn("if(true)")
                .addLn("return true;", 5);
        blocks_to_test.put(builder.toString(), new NamePosition("For tests", new SinglePosition(4, 14)));
        builder = new MyStringBuilder();
        builder.addLn("while(true) {")
                .addLn("System.out.println();", 4)
                .addLn("System.out.println();", 4)
                .addLn("return true;", 6)
                .addLn("}");
        blocks_to_test.put(builder.toString(), new NamePosition("For tests", new SinglePosition(6, 15)));
        builder = new MyStringBuilder();
        builder.addLn("{")
                .addLn("System.out.println();", 4)
                .addLn("System.out.println();", 4)
                .addLn("return true;", 5)
                .addLn("}");
        blocks_to_test.put(builder.toString(), new NamePosition("For tests", new SinglePosition(6, 14)));
        builder = new MyStringBuilder();
        builder.addLn("int i = 0;")
                .addLn("switch (i) {")
                .addLn("case 0:", 4)
                .addLn("System.out.println();", 4)
                .addLn("return true;", 8)
                .addLn("break;", 8)
                .addLn("case 1:", 4)
                .addLn("return false;", 8)
                .addLn("break;", 8)
                .addLn("default:", 4)
                .addLn("return false;", 8)
                .addLn("break;", 8)
                .addLn("}");
        blocks_to_test.put(builder.toString(), new NamePosition("For tests", new SinglePosition(6, 13)));
        for (Entry<String, Position> s : blocks_to_test.entrySet()) {
            String wrapper = wrap(s.getKey());
            Report r = new Indentation(new StringProvider("For tests", wrapper)).run();
            TestUtils.checkIsWarningEmpty(r);
            TestUtils.reportContainsOnlyPositions(r.getErrors(), s.getValue());
        }
    }

    @Test
    void testForTryCatchBlocksWorksForMultipleCatch() {
        String[] blocks_to_test = new String[3];
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("try {")
                .addLn("System.out.println();", 4)
                .addLn("return true;", 4)
                .addLn("} catch (Exception e) {")
                .addLn("return false;", 4)
                .addLn("}");
        blocks_to_test[0] = builder.toString();
        builder = new MyStringBuilder();
        builder.addLn("try {")
                .addLn("System.out.println();", 4)
                .addLn("return true;", 4)
                .addLn("} catch (Exception e) {")
                .addLn("return false;", 4)
                .addLn("} catch (NullPointerException n) {")
                .addLn("return false;", 4)
                .addLn("}");
        blocks_to_test[1] = builder.toString();
        builder = new MyStringBuilder();
        builder.addLn("try {")
                .addLn("System.out.println();", 4)
                .addLn("return true;", 4)
                .addLn("}")
                .addLn("catch (Exception e) {")
                .addLn("return false;", 4)
                .addLn("}")
                .addLn("catch (NullPointerException n) {")
                .addLn("return false;", 4)
                .addLn("}");
        blocks_to_test[2] = builder.toString();
        for (String s : blocks_to_test) {
            String wrapper = wrap(s);
            TestUtils.checkIsEmptyReport(new Indentation(new StringProvider("For tests", wrapper)).run());
        }
    }

    @Test
    void testForTryCatchBlocksNotAlignedFails() {
        Map<String, Position> blocks_to_test = new HashMap<>();
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("try {")
                .addLn("System.out.println();", 5)
                .addLn("System.out.println();", 4)
                .addLn("return true;", 4)
                .addLn("} catch (Exception e) {")
                .addLn("return false;", 4)
                .addLn("}");
        blocks_to_test.put(builder.toString(), new NamePosition("For tests", new SinglePosition(4, 14)));
        for (Entry<String, Position> s : blocks_to_test.entrySet()) {
            String wrapper = wrap(s.getKey());
            Report r = new Indentation(new StringProvider("For tests", wrapper)).run();
            TestUtils.checkIsWarningEmpty(r);
            TestUtils.reportContainsOnlyPositions(r.getErrors(), s.getValue());
        }
    }

    @Test
    void twoAlignedButDifferentBlocksFails() {
        MyStringBuilder b1 = new MyStringBuilder();
        b1.addLn("try {")
                .addLn("System.out.println();", 4)
                .addLn("return true;", 4)
                .addLn("} catch (Exception e) {")
                .addLn("return false;", 4)
                .addLn("}");
        MyStringBuilder b2 = new MyStringBuilder();
        b2.addLn("while(true) {")
                .addLn("System.out.println();", 8)
                .addLn("return true;", 8)
                .addLn("}");
        String wrapper = wrap(new String[]{b1.toString(), b2.toString()});
        Report r = new Indentation(new StringProvider("For tests", wrapper)).run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), new NamePosition("For tests",
                new Range(new SinglePosition(10, 17), new SinglePosition(11, 17))));
    }

    @Test void blockWithSomeWrongIndentationFails() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("if(true) {")
                .addLn("System.out.println();", 4)
                .addLn("System.out.println();", 5)
                .addLn("System.out.println();", 4)
                .addLn("System.out.println();", 5)
                .addLn("System.out.println();", 4)
                .addLn("return true;", 4)
                .addLn("}");
        String wrapper = wrap(builder.toString());
        Report r = new Indentation(new StringProvider("For tests", wrapper)).run();
        MultiplePosition multiplePosition = new MultiplePosition();
        multiplePosition.add(new NamePosition("For tests", new SinglePosition(5, 14)));
        multiplePosition.add(new NamePosition("For tests", new SinglePosition(7, 14)));
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), multiplePosition);
    }

    @Test void cannotInferBlockDiffWhenDifferentBlocks() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("System.out.println(\"\");", 4)
            .addLn("return true;", 4);
        String wrapper = wrap(builder.toString());
        Report r = new Indentation(new StringProvider("For tests", wrapper)).run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(),
                new NamePosition("For tests", new SinglePosition(2, 5)),
                new NamePosition("For tests", new Range(new SinglePosition(3, 13), new SinglePosition(4, 13))));
    }

    @Test void cannotInferBlockDiffWhenMultipleDifferentBlocks() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("if(true) {", 4)
                .addLn("while(true) {", 8)
                .addLn("System.out.println(\"\");", 16)
                .addLn("}", 8)
                .addLn("}", 4)
                .addLn("return true;", 4);
        String wrapper = wrap(builder.toString());
        Report r = new Indentation(new StringProvider("For tests", wrapper)).run();
        MultiplePosition multiplePosition1 = new MultiplePosition();
        multiplePosition1.add(new NamePosition("For tests", new SinglePosition(2, 5)));
        multiplePosition1.add(new NamePosition("For tests", new SinglePosition(4, 17)));
        MultiplePosition multiplePosition2 = new MultiplePosition();
        multiplePosition2.add(new NamePosition("For tests", new Range(new SinglePosition(3, 13), new SinglePosition(8, 13))));
        multiplePosition2.add(new NamePosition("For tests", new SinglePosition(5, 25)));
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), multiplePosition1, multiplePosition2);
    }

    @Test void multipleWrongBlocksReportError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("if(true) {")
                .addLn("while(true) {", 4)
                .addLn("System.out.println(\"\");", 16)
                .addLn("System.out.println(\"\");", 16)
                .addLn("if(true) {", 16)
                .addLn("System.out.println(\"\");", 28)
                .addLn("}", 16)
                .addLn("}", 4)
                .addLn("}")
                .addLn("return true;");
        String wrapper = wrap(builder.toString());
        Report r = new Indentation(new StringProvider("For tests", wrapper)).run();
        MultiplePosition multiplePosition = new MultiplePosition();
        multiplePosition.add(new NamePosition("For tests", new Range(5, 25, 7, 25)));
        multiplePosition.add(new NamePosition("For tests", new SinglePosition(8, 37)));
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), multiplePosition);
    }

    @Test void cannotInferBlockTypeWhenMultiplePossibleIndentation() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("System.out.println(\"\");")
            .addLn("System.out.println(\"\");", 4)
            .addLn("System.out.println(\"\");")
            .addLn("return true;", 4);
        String wrapper = wrap(builder.toString());
        Report r = new Indentation(new StringProvider("For tests", wrapper)).run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(),
                new NamePosition("For tests", new Range(new SinglePosition(3, 9), new SinglePosition(6, 13))));
    }

    @Test void emptyBlockDoesNotFail() {
        String wrapper = wrap("");
        ContentProvider contentProvider = new StringProvider("For tests", wrapper);
        TestUtils.checkIsEmptyReport(new Indentation(contentProvider).run());
    }

    @Test void elseIfBracketWillWork() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("if(true) {")
                .addLn("return true;", 4)
                .addLn("} else if(false) {")
                .addLn("return false;", 4)
                .addLn("} else {")
                .addLn("return true;", 4)
                .addLn("}");
        String wrapper = wrap(builder.toString());
        TestUtils.checkIsEmptyReport(new Indentation(new StringProvider("For tests", wrapper)).run());
    }

    @Test void elseIfNoBracketWillWork() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("if(true)")
                .addLn("return true;", 4)
                .addLn("else if(false)")
                .addLn("return false;", 4)
                .addLn("else")
                .addLn("return true;", 4);
        String wrapper = wrap(builder.toString());
        TestUtils.checkIsEmptyReport(new Indentation(new StringProvider("For tests", wrapper)).run());
    }

    @Test void elseIfBracketFailsForIndentError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("if(true) {")
                .addLn("return true;", 4)
                .addLn("} else if(false) {")
                .addLn(" return false;", 4)
                .addLn("} else {")
                .addLn("return true;", 4)
                .addLn("}");
        String wrapper = wrap(builder.toString());
        Report r = new Indentation(new StringProvider("For tests", wrapper)).run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), new NamePosition("For tests", new SinglePosition(6, 14)));
    }

    @Test void elseIfNoBracketFailsForIndentError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("if(true)")
                .addLn("return true;", 4)
                .addLn("else if(false)")
                .addLn(" return false;", 4)
                .addLn("else")
                .addLn("return true;", 4);
        String wrapper = wrap(builder.toString());
        Report r = new Indentation(new StringProvider("For tests", wrapper)).run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), new NamePosition("For tests", new SinglePosition(6, 14)));
    }

    @Test void innerClassThrowsErrorWhenMisaligned() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("public class Test {")
                .addLn("public static boolean test() {", 4)
                .addLn("return true;", 8)
                .addLn("}", 4)
                .addLn("public enum Test2 {", 4)
                .addLn(" MISALIGNED", 8)
                .addLn("}", 4)
                .addLn("}");
        Report r = new Indentation(new StringProvider("For tests", builder.toString())).run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), new NamePosition("For tests", new SinglePosition(6, 10)));
    }

    @Test void enumOnSingleLineDoesNotFail() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("public class Test {")
                .addLn("public static boolean test() {", 4)
                .addLn("return true;", 8)
                .addLn("}", 4)
                .addLn("public enum Test2 {", 4)
                .addLn("TEST, TEST2, TEST3, TEST4", 8)
                .addLn("}", 4)
                .addLn("}");
        TestUtils.checkIsEmptyReport(new Indentation(new StringProvider("For tests", builder.toString())).run());
    }

    @Test void emptyMethodDoesNotFail() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("public abstract class Test {")
                .addLn("public abstract boolean test();", 4)
                .addLn("}");
        TestUtils.checkIsEmptyReport(new Indentation(new StringProvider("For tests", builder.toString())).run());
    }

    private String wrap(String s) {
        String[] arr = {s};
        return wrap(arr);
    }

    private String wrap(String[] s) {
        MyStringBuilder blocks = new MyStringBuilder();
        for (String b : s) {
            blocks.addLn(b, 8);
        }
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("public class Test {")
                .addLn("public static boolean test() {", 4)
                .addLn(blocks.toString())
                .addLn("}", 4)
                .addLn("}");
        return builder.toString();
    }
}