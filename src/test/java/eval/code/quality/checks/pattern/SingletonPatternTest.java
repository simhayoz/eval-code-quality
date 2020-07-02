package eval.code.quality.checks.pattern;

import eval.code.quality.MyStringBuilder;
import eval.code.quality.TestUtils;
import eval.code.quality.checks.Report;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.FileProvider;
import eval.code.quality.provider.StringProvider;
import org.junit.jupiter.api.Test;

import java.io.File;

public class SingletonPatternTest {

    @Test void singletonPatternWithLazyDoesNotFail() {
        ContentProvider singletonProvider = new FileProvider(new File("assets/examples/ExampleSingletonPatternWithLazy.java"));
        TestUtils.checkIsEmptyReport(new SingletonPattern(singletonProvider, "Connection").run());
    }

    @Test void singletonPatternDoesNotFail() {
        ContentProvider singletonProvider = new FileProvider(new File("assets/examples/ExampleSingletonPattern.java"));
        TestUtils.checkIsEmptyReport(new SingletonPattern(singletonProvider, "Stack").run());
    }

    @Test void basicClassesFails() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("public class Test {")
                .addLn("public static boolean test() {", 4)
                .addLn("return true;", 8)
                .addLn("}", 4)
                .addLn("}");
        ContentProvider contentProvider = new StringProvider("For tests", builder.toString());
        Report r = new SingletonPattern(contentProvider, "Test").run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.checkNotIsErrorEmpty(r);
    }
}
