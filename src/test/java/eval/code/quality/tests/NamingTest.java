package eval.code.quality.tests;

import eval.code.quality.MyStringBuilder;
import eval.code.quality.position.MultiplePosition;
import eval.code.quality.position.NamePosition;
import eval.code.quality.position.Position;
import eval.code.quality.position.SinglePosition;
import eval.code.quality.provider.StringProvider;
import eval.code.quality.utils.Error;
import eval.code.quality.utils.MultiplePossibility;
import eval.code.quality.utils.ReportPosition;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class NamingTest {

    @Test void emptyCUReportNoError() {
        Report r = new Naming(new StringProvider("tests", "")).run();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), is(empty()));
    }

    @Test void classWithSameModifierNoError() {
        MyStringBuilder sb = new MyStringBuilder();
        sb.addLn("public class Test {").
                addLn("}").
                addLn("public class TestForLongerClass {").
                addLn("}").
                addLn("public class TestStillWorking {").
                addLn("}");
        Report r = new Naming(new StringProvider("tests", sb.toString())).run();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), is(empty()));
    }

    @Test void classVarWorkWithSameStyle() {
        MyStringBuilder sb = new MyStringBuilder();
        sb.addLn("public class Test {").
                addLn("private final static String TEST = 2;", 4).
                addLn("private final static String TEST_EVEN_LONGER = 2;", 4).
                addLn("private final static String WORKING;", 4).
                addLn("private final static String WORKING;", 4).
                addLn("public String _other_style;", 4).
                addLn("private String evenWithOtherStyle;", 4).
                addLn("}");
        Report r = new Naming(new StringProvider("tests", sb.toString())).run();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), is(empty()));
    }

    @Test void methodNamesWorkWithSameStyle() {
        MyStringBuilder sb = new MyStringBuilder();
        sb.addLn("public class Test {").
                addLn("public Test() {", 4).
                addLn("// Constructor don't produce error", 8).
                addLn("}", 4).
                addLn("public static void test() {", 4).
                addLn("}", 4).
                addLn("public static void getNothing() {", 4).
                addLn("}", 4).
                addLn("public static void workForLong() {", 4).
                addLn("}", 4).
                addLn("public static void thisisatest() {", 4).
                addLn("}", 4).
                addLn("}");
        Report r = new Naming(new StringProvider("tests", sb.toString())).run();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), is(empty()));
    }

    @Test void methodVarWorkWithSameStyle() {
        MyStringBuilder sb = new MyStringBuilder();
        sb.addLn("public class Test {").
                addLn("public static void test() {", 4).
                addLn("int _this_is_a_var = 0;", 8).
                addLn("String _s = \"test\";", 8).
                addLn("}", 4).
                addLn("public static void getNothing() {", 4).
                addLn("}", 4).
                addLn("public static void workForLong() {", 4).
                addLn("}", 4).
                addLn("public static void thisisatest() {", 4).
                addLn("boolean _istrue = true;", 8).
                addLn("long _this_is_long = 22;", 8).
                addLn("int _even_empty;", 8).
                addLn("}", 4).
                addLn("}");
        Report r = new Naming(new StringProvider("tests", sb.toString())).run();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), is(empty()));
    }

    @Test void notSameStyleProduceError() {
        MyStringBuilder sb = new MyStringBuilder();
        sb.addLn("public class Test {").
                addLn("public static void test() {", 4).
                addLn("}", 4).
                addLn("public static void getNothing() {", 4).
                addLn("}", 4).
                addLn("public static void _workForLong() {", 4).
                addLn("int oneStyle;", 8).
                addLn("int another_style;", 8).
                addLn("}", 4).
                addLn("public static void thisisatest() {", 4).
                addLn("}", 4).
                addLn("public static void thisisaothertest() {", 4).
                addLn("}", 4).
                addLn("public static void _workForLongerMethodName() {", 4).
                addLn("}", 4).
                addLn("}");
        Report r = new Naming(new StringProvider("tests", sb.toString())).run();
        List<Position> expected = new ArrayList<>();
        expected.add(new NamePosition("tests", new SinglePosition(7, 9)));
        expected.add(new NamePosition("tests", new SinglePosition(8, 9)));
        Error error1 = MultiplePossibility.at(expected);
        MultiplePosition expectedMultiple = new MultiplePosition();
        expectedMultiple.add(new NamePosition("tests", new SinglePosition(6, 5)));
        expectedMultiple.add(new NamePosition("tests", new SinglePosition(14, 5)));
        Error error2 = ReportPosition.at(expectedMultiple);
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), Matchers.<Collection<Error>>allOf(
                hasItems(is(error1),
                        is(error2)),
                hasSize(2)));
    }

    @Test void enumCanGenerateError() {
        MyStringBuilder sb = new MyStringBuilder();
        sb.addLn("public class Test {")
                .addLn("public enum _TEST {", 4)
                .addLn("TEST, TEST_WITH_UNDERSCORE", 8)
                .addLn("}", 4)
                .addLn("public enum _TeST2 {", 4)
                .addLn("test_with, TEST_WITH_UNDERSCORE", 8)
                .addLn("}", 4)
                .addLn("}");
        List<Position> expected = new ArrayList<>();
        expected.add(new NamePosition("tests", new SinglePosition(2, 5)));
        expected.add(new NamePosition("tests", new SinglePosition(5, 5)));
        Error error = MultiplePossibility.at(expected);
        Report r = new Naming(new StringProvider("tests", sb.toString())).run();
        assertThat(r.getErrors(), Matchers.<Collection<Error>>allOf(
                hasItems(is(ReportPosition.at(new NamePosition("tests", new SinglePosition(6, 9)))),
                        is(error)),
                hasSize(2)));
    }

    @Test void errorWhenNotSameStyleInsideMethod() {
        MyStringBuilder sb = new MyStringBuilder();
        sb.addLn("public class Test {").
                addLn("public static void getNothing() {", 4).
                addLn("}", 4).
                addLn("public static void workForLong() {", 4).
                addLn("int oneStyle;", 8).
                addLn("int another_style;", 8).
                addLn("}", 4).
                addLn("public static void thisIsATest() {", 4).
                addLn("boolean same_other_style = true;", 8).
                addLn("}", 4).
                addLn("}");
        Report r = new Naming(new StringProvider("tests", sb.toString())).run();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), Matchers.<Collection<Error>>allOf(
                hasItem(is(ReportPosition.at(new NamePosition("tests", new SinglePosition(5, 9))))),
                hasSize(1)));
    }

    @Test void uniqueNamingCannotBug() {
        MyStringBuilder sb = new MyStringBuilder();
        sb.addLn("public class Test {").
                addLn("public static void test() {", 4).
                addLn("}", 4).
                addLn("}");
        Report r = new Naming(new StringProvider("tests", sb.toString())).run();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), is(empty()));
    }

    @Test void simplePropertyShouldNotTriggerError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("class Class {")
                .addLn("int mId;", 4)
                .addLn("int mName;", 4)
                .addLn("}");
        Report r = new Naming(new StringProvider("tests", builder.toString())).run();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), is(empty()));
        builder = new MyStringBuilder();
        builder.addLn("class Class { }")
                .addLn("class Class2 { }")
                .addLn("class Class2D { }");
        r = new Naming(new StringProvider("tests", builder.toString())).run();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), is(empty()));
    }

    @Test void shouldFailUpperClass() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("class Class { }")
                .addLn("class Class1 { }")
                .addLn("class Class2 { }")
                .addLn("class class3 { }");
        Report r = new Naming(new StringProvider("tests", builder.toString())).run();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), Matchers.<Collection<Error>>allOf(
                hasItem(is(ReportPosition.at(new NamePosition("tests", new SinglePosition(4, 1))))),
                hasSize(1)));
    }
}
