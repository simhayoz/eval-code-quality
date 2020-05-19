package eval.code.quality.checks;

import eval.code.quality.MyStringBuilder;
import eval.code.quality.TestUtils;
import eval.code.quality.position.*;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.StringProvider;
import org.junit.jupiter.api.Test;

public class BracesTest {

    @Test void emptyCUReportNoError() {
        ContentProvider contentProvider = new StringProvider("Empty String", "");
        TestUtils.checkIsEmptyReport(new Braces(contentProvider).run());
    }

    @Test void simpleBlocksThrowsNoError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("for(int i=0; i < 2; ++i) {")
                .addLn("return true;", 4)
                .addLn("}")
                .addLn("if(true) {")
                .addLn("return true;", 4)
                .addLn("} else if(false) {")
                .addLn("return false;", 4)
                .addLn("} else {")
                .addLn("return true;", 4)
                .addLn("}")
                .addLn("while(true) {")
                .addLn("System.out.println();", 4)
                .addLn("return true;", 4)
                .addLn("}")
                .addLn("{")
                .addLn("System.out.println();", 4)
                .addLn("return true;", 4)
                .addLn("}")
                .addLn("int i = 0;")
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
        String wrapper = wrap(builder.toString());
        TestUtils.checkIsEmptyReport(new Braces(new StringProvider("For tests", wrapper)).run());
    }

    @Test void simpleTryForEachAndDoStmtThrowsNoError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("try {")
                .addLn("System.out.println();", 4)
                .addLn("System.out.println();", 4)
                .addLn("} catch(IOException e) {")
                .addLn("return true;", 4)
                .addLn("} catch (Exception e) {")
                .addLn("return false;", 4)
                .addLn("}")
                .addLn("do {")
                .addLn("System.out.println();", 4)
                .addLn("} while(true);")
                .addLn("for(char c: \"test\") {")
                .addLn("System.out.println(c);", 4)
                .addLn("}");
        String wrapper = wrap(builder.toString());
        TestUtils.checkIsEmptyReport(new Braces(new StringProvider("For tests", wrapper)).run());
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
        TestUtils.checkIsEmptyReport(new Braces(new StringProvider("For tests", wrapper)).run());
    }

    @Test void notAlignedChildThrowsError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("public class Test {")
                .addLn("public static boolean test() {", 4)
                .addLn("if(true) {", 8)
                .addLn("return true;", 12)
                .addLn("}", 8)
                .addLn(" else if(false) {", 8)
                .addLn("return false;", 12)
                .addLn("}", 8)
                .addLn("else {", 8)
                .addLn("return true;", 12)
                .addLn("}", 8)
                .addLn("}", 4)
                .addLn("}");
        Report r = new Braces(new StringProvider("For tests", builder.toString())).run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), new NamePosition("For tests", new SinglePosition(6, 10)));
    }

    @Test void oneLinerBlockDifferentThrowsError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("for(int i=0; i < 2; ++i) {")
                .addLn("return true;", 4)
                .addLn("}")
                .addLn("if(true) {")
                .addLn("return true;", 4)
                .addLn("} else if(false) {")
                .addLn("return false;", 4)
                .addLn("} else {")
                .addLn("return true;", 4)
                .addLn("}")
                .addLn("while(true)")
                .addLn("System.out.println();", 4)
                .addLn("if(true)")
                .addLn("return true;", 4);
        String wrapper = wrap(builder.toString());
        Report r = new Braces(new StringProvider("For tests", wrapper)).run();
        MultiplePosition multiplePosition = new MultiplePosition();
        multiplePosition.add(new NamePosition("For tests", new SinglePosition(14, 13)));
        multiplePosition.add(new NamePosition("For tests", new SinglePosition(16, 13)));
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), multiplePosition);
    }

    @Test void bracketMoreThanOneLineAfterThrowsError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("public class Test")
                .addLn("{")
                .addLn("public static boolean test()", 4)
                .addLn("{", 4)
                .addLn("if(true) ", 8)
                .addBlankLine()
                .addBlankLine()
                .addBlankLine()
                .addLn("{", 8)
                .addLn("return true;", 12)
                .addLn("}", 8)
                .addLn("else if(false)", 8)
                .addLn("{", 8)
                .addLn("return false;", 12)
                .addLn("}", 8)
                .addLn("else", 8)
                .addLn("{", 8)
                .addLn("return true;", 12)
                .addLn("}", 8)
                .addLn("}", 4)
                .addLn("}");
        Report r = new Braces(new StringProvider("For tests", builder.toString())).run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), new NamePosition("For tests", new SinglePosition(9, 9)));
    }

    @Test void childMoreThanOneLineAfterThrowsError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("public class Test")
                .addLn("{")
                .addLn("public static boolean test()", 4)
                .addLn("{", 4)
                .addLn("if(true) ", 8)
                .addLn("{", 8)
                .addLn("return true;", 12)
                .addLn("}", 8)
                .addBlankLine()
                .addBlankLine()
                .addBlankLine()
                .addLn("else if(false)", 8)
                .addLn("{", 8)
                .addLn("return false;", 12)
                .addLn("}", 8)
                .addLn("else", 8)
                .addLn("{", 8)
                .addLn("return true;", 12)
                .addLn("}", 8)
                .addLn("}", 4)
                .addLn("}");
        Report r = new Braces(new StringProvider("For tests", builder.toString())).run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), new NamePosition("For tests", new SinglePosition(12, 9)));
    }

    @Test void oneLinerBlockDifferentThrowsMultipleError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("for(int i=0; i < 2; ++i) {")
                .addLn("return true;", 4)
                .addLn("}")
                .addLn("if(true) {")
                .addLn("return true;", 4)
                .addLn("} else if(false) {")
                .addLn("return false;", 4)
                .addLn("} else")
                .addLn("return true;", 4)
                .addLn("while(true)")
                .addLn("System.out.println();", 4)
                .addLn("if(true)")
                .addLn("return true;", 4);
        String wrapper = wrap(builder.toString());
        Report r = new Braces(new StringProvider("For tests", wrapper)).run();
        MultiplePosition multiplePosition1 = new MultiplePosition();
        multiplePosition1.add(new NamePosition("For tests", new SinglePosition(3, 34)));
        multiplePosition1.add(new NamePosition("For tests", new SinglePosition(6, 18)));
        multiplePosition1.add(new NamePosition("For tests", new SinglePosition(8, 26)));
        MultiplePosition multiplePosition2 = new MultiplePosition();
        multiplePosition2.add(new NamePosition("For tests", new SinglePosition(11, 13)));
        multiplePosition2.add(new NamePosition("For tests", new SinglePosition(13, 13)));
        multiplePosition2.add(new NamePosition("For tests", new SinglePosition(15, 13)));
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), multiplePosition1, multiplePosition2);
    }

    @Test void differentStartPropertyThrowsError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("for(int i=0; i < 2; ++i) {")
                .addLn("return true;", 4)
                .addLn("}")
                .addLn("if(true) ")
                .addLn("{")
                .addLn("return true;", 4)
                .addLn("} else if(false) {")
                .addLn("return false;", 4)
                .addLn("} else {")
                .addLn("return true;", 4)
                .addLn("}")
                .addLn("while(true) {")
                .addLn("System.out.println();", 4)
                .addLn("}")
                .addLn("if(true) {")
                .addLn("return true;", 4)
                .addLn("}");
        String wrapper = wrap(builder.toString());
        Report r = new Braces(new StringProvider("For tests", wrapper)).run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), new NamePosition("For tests", new SinglePosition(6, 9)));
    }

    @Test void notAlignedOpeningBracketThrowsError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("public class Test")
                .addLn("{")
                .addLn("public static boolean test()", 4)
                .addLn("{", 4)
                .addLn("if(true)", 8)
                .addLn(" {", 8)
                .addLn("return false;", 12)
                .addLn("}", 8)
                .addLn("if(true)", 8)
                .addLn("{", 8)
                .addLn("return false;", 12)
                .addLn("}", 8)
                .addLn("}", 4)
                .addLn("}");
        Report r = new Braces(new StringProvider("For tests", builder.toString())).run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), new NamePosition("For tests", new SinglePosition(6, 10)));
    }

    @Test void notAlignedClosingBracketThrowsError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("public class Test")
                .addLn("{")
                .addLn("public static boolean test()", 4)
                .addLn("{", 4)
                .addLn("if(true)", 8)
                .addLn("{", 8)
                .addLn("return false;", 12)
                .addLn(" }", 8)
                .addLn("if(true)", 8)
                .addLn("{", 8)
                .addLn("return false;", 12)
                .addLn("}", 8)
                .addLn("}", 4)
                .addLn("}");
        Report r = new Braces(new StringProvider("For tests", builder.toString())).run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), new NamePosition("For tests", new SinglePosition(8, 10)));
    }

    @Test void differentPropertiesThrowsError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("if(true) {")
                .addLn("return true;", 4)
                .addLn("} else if(false) {")
                .addLn("return false;", 4)
                .addLn("} else {")
                .addLn("return true;", 4)
                .addLn("}")
                .addLn("if(true) {")
                .addLn("return true;", 4)
                .addLn("} else if(false) ")
                .addLn("{")
                .addLn("return false;", 4)
                .addLn("} else {")
                .addLn("return true;", 4)
                .addLn("}")
                .addLn("if(true) {")
                .addLn("return true;", 4)
                .addLn("} else if(false) {")
                .addLn("return false;", 4)
                .addLn("} else {")
                .addLn("return true;", 4)
                .addLn("}");
        String wrapper = wrap(builder.toString());
        Report r = new Braces(new StringProvider("For tests", wrapper)).run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), new NamePosition("For tests", new SinglePosition(12, 11)));
    }

    @Test void differentEndPropertyThrowsError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("if(true)")
                .addLn("return true;", 4)
                .addLn("else if(false) {")
                .addLn("return false;", 4)
                .addLn("} else")
                .addLn("return true;", 4)
                .addLn("if(true)")
                .addLn("return true;", 4)
                .addLn("else if(false) {")
                .addLn("return false;", 4)
                .addLn("}")
                .addLn("else")
                .addLn("return true;", 4)
                .addLn("if(true)")
                .addLn("return true;", 4)
                .addLn("else if(false) {")
                .addLn("return false;", 4)
                .addLn("}")
                .addLn("else")
                .addLn("return true;", 4);
        String wrapper = wrap(builder.toString());
        Report r = new Braces(new StringProvider("For tests", wrapper)).run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsPositions(r.getErrors(), new NamePosition("For tests", new SinglePosition(7, 11)));
    }

    @Test void annotationDoesNotCauseError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("public class Test {")
                .addLn("@Override", 4)
                .addLn("public String toString() {", 4)
                .addLn("return \"a string\";", 8)
                .addLn("}", 4)
                .addLn("}");
        TestUtils.checkIsEmptyReport(new Braces(new StringProvider("For tests", builder.toString())).run());
    }

    @Test void simpleIfElseTestTriggerError() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("class Class {")
                .addLn("void m() {", 4)
                .addLn("if (true) {", 8)
                .addLn("System.out.println(\"True\");", 12)
                .addLn("}", 8)
                .addLn("else // mismatched style with the rest of the class", 8)
                .addLn("{", 8)
                .addLn("System.out.println(\"False\");", 12)
                .addLn("}", 8)
                .addLn("}", 4)
                .addLn("}");
        Report r = new Braces(new StringProvider("For tests", builder.toString())).run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.reportContainsOnlyPositions(r.getErrors(), new NamePosition("For tests", new SinglePosition(6, 9)));
    }

    @Test void annotationBeforeParentDoesNotFail() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("public class Test")
                .addLn("{")
                .addLn("@Override", 4)
                .addLn("public String toString()", 4)
                .addLn("{", 4)
                .addLn("return \"a string\";", 8)
                .addLn("}", 4)
                .addLn("}");
        TestUtils.checkIsEmptyReport(new Braces(new StringProvider("For tests", builder.toString())).run());
    }

    @Test void multiLineParametersDoesNotFail() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("public class Test")
                .addLn("{")
                .addLn("public String toString(String s,", 4)
                .addLn("String s2,", 8)
                .addLn("String s3", 8)
                .addLn(")", 4)
                .addLn("{", 4)
                .addLn("return \"a string\";", 8)
                .addLn("}", 4)
                .addLn("}");
        System.out.println(builder);
        TestUtils.checkIsEmptyReport(new Braces(new StringProvider("For tests", builder.toString())).run());
    }

    @Test void multiLineEndSameLineParametersDoesNotFail() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("public class Test")
                .addLn("{")
                .addLn("public Test(", 4)
                .addLn("String s,", 8)
                .addLn("String s2,", 8)
                .addLn("String s3)", 8)
                .addLn("{", 4)
                .addLn("return \"a string\";", 8)
                .addLn("}", 4)
                .addLn("}");
        System.out.println(builder);
        TestUtils.checkIsEmptyReport(new Braces(new StringProvider("For tests", builder.toString())).run());
    }

    @Test void annotationBeforeClassDoesNotFail() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("@TODO(\"Test\")")
                .addLn("public class Test")
                .addLn("{")
                .addLn("public String toString()", 4)
                .addLn("{", 4)
                .addLn("return \"a string\";", 8)
                .addLn("}", 4)
                .addLn("}");
        TestUtils.checkIsEmptyReport(new Braces(new StringProvider("For tests", builder.toString())).run());
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
