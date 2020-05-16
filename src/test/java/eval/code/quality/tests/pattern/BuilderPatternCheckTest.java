package eval.code.quality.tests.pattern;

import eval.code.quality.TestUtils;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.FileProvider;
import org.junit.jupiter.api.Test;

import java.io.File;

public class BuilderPatternCheckTest {

    @Test void builderPatternDoesNotFail() {
        ContentProvider builderProvider = new FileProvider(new File("assets/tests/ExampleBuilderPattern.java"));
        TestUtils.checkIsEmptyReport(new BuilderPatternTest(builderProvider, "Hero", "Hero$Builder").run());
    }
}
