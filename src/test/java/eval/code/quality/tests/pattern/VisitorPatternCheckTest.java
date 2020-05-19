package eval.code.quality.tests.pattern;

import eval.code.quality.MyStringBuilder;
import eval.code.quality.TestUtils;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.DirectoryProvider;
import eval.code.quality.provider.MultipleContentProvider;
import eval.code.quality.provider.StringProvider;
import eval.code.quality.tests.Report;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class VisitorPatternCheckTest {

    @Test void visitorPatternDoesNotFail() {
        ContentProvider visitorProvider = new DirectoryProvider("assets/tests/ExampleVisitor");
        List<String> childrenName = List.of("Book", "Fruit");
        Report r = new VisitorPatternTest(visitorProvider, "Item", childrenName, "Visitor").run();
        TestUtils.checkIsEmptyReport(r);
    }

    @Test void basicClassFails() {
        MyStringBuilder visitor = new MyStringBuilder();
        visitor.addLn("public class Visitor {")
                .addLn("public static boolean test() {", 4)
                .addLn("return true;", 8)
                .addLn("}", 4)
                .addLn("}");
        MyStringBuilder parent = new MyStringBuilder();
        parent.addLn("public class Parent {")
                .addLn("public static boolean test() {", 4)
                .addLn("return true;", 8)
                .addLn("}", 4)
                .addLn("}");
        MyStringBuilder child = new MyStringBuilder();
        child.addLn("public class Child extends Parent {")
                .addLn("public static boolean test() {", 4)
                .addLn("return true;", 8)
                .addLn("}", 4)
                .addLn("}");
        ContentProvider contentProvider = MultipleContentProvider.fromContentProvider(
                new StringProvider("1st", visitor.toString()),
                new StringProvider("2nd", parent.toString()),
                new StringProvider("3rd", child.toString()));
        Report r = new VisitorPatternTest(contentProvider, "Parent", Collections.singletonList("Child"), "Visitor").run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.checkNotIsErrorEmpty(r);
    }
}
