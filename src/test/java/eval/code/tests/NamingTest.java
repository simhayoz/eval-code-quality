package eval.code.tests;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import eval.code.MySBuilder;
import eval.code.tools.ProcessCU;

public class NamingTest {

    @Test
    void emptyCUReportNoError() {
        Report r = new Naming(ProcessCU.fromString("").getCU(), "").runTest();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), is(empty()));
    }

    @Test
    void classWithSameModifierNoError() {
        MySBuilder sb = new MySBuilder();
        sb.addLn("public class Test {").
            addLn("}").
            addLn("public class TestForLongerClass {").
            addLn("}").
            addLn("public class TestStillWorking {").
            addLn("}");
        Report r = new Naming(ProcessCU.fromString(sb.toString()).getCU(), sb.toString()).runTest();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), is(empty()));
    }

    @Test
    void classVarWorkWithSameStyle() {
        MySBuilder sb = new MySBuilder();
        sb.addLn("public class Test {").
            addLn("private final static String TEST = 2;", 4).
            addLn("private final static String TEST_EVEN_LONGER = 2;", 4).
            addLn("private final static String WORKING;", 4).
            addLn("private final static String WORKING;", 4).
            addLn("public String _other_style;", 4).
            addLn("private String evenWithOtherStyle;", 4).
            addLn("}");
        Report r = new Naming(ProcessCU.fromString(sb.toString()).getCU(), sb.toString()).runTest();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), is(empty()));
    }

    @Test
    void methodNamesWorkWithSameStyle() {
        MySBuilder sb = new MySBuilder();
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
        Report r = new Naming(ProcessCU.fromString(sb.toString()).getCU(), sb.toString()).runTest();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), is(empty()));
    }

    @Test
    void methodVarWorkWithSameStyle() {
        MySBuilder sb = new MySBuilder();
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
        Report r = new Naming(ProcessCU.fromString(sb.toString()).getCU(), sb.toString()).runTest();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), is(empty()));
    }

    @Test
    void notSameStyleProduceError() {
        MySBuilder sb = new MySBuilder();
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
            addLn("}");
        Report r = new Naming(ProcessCU.fromString(sb.toString()).getCU(), sb.toString()).runTest();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), hasSize(2));
    }

    @Test
    void uniqueNamingCannotBug() {
        MySBuilder sb = new MySBuilder();
        sb.addLn("public class Test {").
            addLn("public static void test() {", 4).
            addLn("}", 4).
            addLn("}");
        Report r = new Naming(ProcessCU.fromString(sb.toString()).getCU(), sb.toString()).runTest();
        assertThat(r.getWarnings(), is(empty()));
        assertThat(r.getErrors(), is(empty()));
    }
}