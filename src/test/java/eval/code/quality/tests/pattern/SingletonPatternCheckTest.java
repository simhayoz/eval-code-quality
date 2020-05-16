package eval.code.quality.tests.pattern;

import eval.code.quality.MyStringBuilder;
import eval.code.quality.TestUtils;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.FileProvider;
import eval.code.quality.provider.StringProvider;
import eval.code.quality.tests.Report;
import org.junit.jupiter.api.Test;

import java.io.File;

public class SingletonPatternCheckTest {

    @Test void singletonPatternDoesNotFail() {
        ContentProvider singletonProvider = new FileProvider(new File("assets/tests/ExampleSingletonPattern.java"));
        TestUtils.checkIsEmptyReport(new SingletonPatternTest(singletonProvider, "IvoryTower").run());
    }

    @Test void singletonPatternWithLazyEvalDoesNotFail() {
        ContentProvider singletonProvider = new FileProvider(new File("assets/tests/ExampleSingletonPatternWithLazyEval.java"));
        TestUtils.checkIsEmptyReport(new SingletonPatternTest(singletonProvider, "IvoryTower").run());
    }

    @Test void basicClassesFails() {
        MyStringBuilder builder = new MyStringBuilder();
        builder.addLn("public class Test {")
                .addLn("public static boolean test() {", 4)
                .addLn("return true;", 8)
                .addLn("}", 4)
                .addLn("}");
        ContentProvider contentProvider = new StringProvider("For tests", builder.toString());
        Report r = new SingletonPatternTest(contentProvider, "Test").run();
        TestUtils.checkIsWarningEmpty(r);
        TestUtils.checkNotIsErrorEmpty(r);
    }
}
