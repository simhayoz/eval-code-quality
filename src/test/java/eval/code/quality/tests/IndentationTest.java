package eval.code.quality.tests;

import eval.code.quality.MyStringBuilder;
import eval.code.quality.position.MultiplePosition;
import eval.code.quality.position.Position;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.StringProvider;
import eval.code.quality.utils.Error;
import eval.code.quality.utils.ReportPosition;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.hamcrest.Matchers;

class IndentationTest {

    @Test
    void emptyCUReportNoError() {
        ContentProvider contentProvider = new StringProvider("Empty String", "");
        Report r = new Indentation(contentProvider).run();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), is(empty()));
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
            Report r = new Indentation(new StringProvider("For tests", wrapper)).run();
            assertThat(r.getWarnings(), is(empty()));
            assertThat(r.getErrors(), is(empty()));
        }
    }

    @Test
    void testForSimpleWhileForIfFailsWhenMisaligned() {
        Map<String, Position> blocks_to_test = new HashMap<>();
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("for(int i=0; i < 2; ++i) {")
                .addLn("return true;", 2)
                .addLn("}");
        blocks_to_test.put(builder.toString(), new Range(new SinglePosition(4, 11), new SinglePosition(4, 11)));
        builder = new MyStringBuilder();
        builder.addLn("if(true) {")
                .addLn("return true;", 4)
                .addLn("} else {")
                .addLn("return false;", 5)
                .addLn("}");
        blocks_to_test.put(builder.toString(), new Range(new SinglePosition(6, 14), new SinglePosition(6, 14)));
        builder = new MyStringBuilder();
        builder.addLn("if(true)")
                .addLn("return true;", 5);
        blocks_to_test.put(builder.toString(), new Range(new SinglePosition(4, 14), new SinglePosition(4, 14)));
        builder = new MyStringBuilder();
        builder.addLn("while(true) {")
                .addLn("System.out.println();", 4)
                .addLn("System.out.println();", 4)
                .addLn("return true;", 6)
                .addLn("}");
        blocks_to_test.put(builder.toString(), new SinglePosition(6, 15));
        builder = new MyStringBuilder();
        builder.addLn("{")
                .addLn("System.out.println();", 4)
                .addLn("System.out.println();", 4)
                .addLn("return true;", 5)
                .addLn("}");
        blocks_to_test.put(builder.toString(), new SinglePosition(6, 14));
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
        blocks_to_test.put(builder.toString(), new SinglePosition(6, 13));
        for (Entry<String, Position> s : blocks_to_test.entrySet()) {
            String wrapper = wrap(s.getKey());
            Report r = new Indentation(new StringProvider("For tests", wrapper)).run();
            assertThat(wrapper, r.getWarnings(), is(empty()));
            assertThat(wrapper, r.getErrors(), Matchers
                    .<Collection<Error>>allOf(hasItem(is(ReportPosition.at(s.getValue()))), hasSize(1)));
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
            Report r = new Indentation(new StringProvider("For tests", wrapper)).run();
            assertThat(wrapper, r.getWarnings(), is(empty()));
            assertThat(wrapper, r.getErrors(), is(empty()));
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
        blocks_to_test.put(builder.toString(), new SinglePosition(4, 14));
//        builder = new MyStringBuilder();
//        builder.addLn("try {")
//                .addLn("System.out.println();", 4)
//                .addLn("return true;", 4)
//                .addLn("}")
//                .addLn("catch (Exception e) {")
//                .addLn("return false;", 4)
//                .addLn("}")
//                .addLn("catch (NullPointerException n) {", 2)
//                .addLn("return false;", 4)
//                .addLn("}");
//        blocks_to_test.put(builder.toString(), new SinglePosition(10, 10));
        for (Entry<String, Position> s : blocks_to_test.entrySet()) {
            String wrapper = wrap(s.getKey());
            Report r = new Indentation(new StringProvider("For tests", wrapper)).run();
            assertThat(wrapper, r.getWarnings(), is(empty()));
            assertThat(wrapper, r.getErrors(), Matchers
                    .<Collection<Error>>allOf(hasItem(is(ReportPosition.at(s.getValue()))), hasSize(1)));
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
        assertThat(wrapper, r.getWarnings(), is(empty()));
        assertThat(wrapper, r.getErrors(),
                Matchers.<Collection<Error>>allOf(
                        hasItem(is(ReportPosition.at(
                                new Range(new SinglePosition(10, 17), new SinglePosition(11, 17))))),
                        hasSize(1)));
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