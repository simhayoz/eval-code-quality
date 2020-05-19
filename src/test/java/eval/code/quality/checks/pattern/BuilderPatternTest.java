package eval.code.quality.checks.pattern;

import eval.code.quality.TestUtils;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.FileProvider;
import org.junit.jupiter.api.Test;

import java.io.File;

public class BuilderPatternTest {

    @Test void builderPatternDoesNotFail() {
        ContentProvider builderProvider = new FileProvider(new File("assets/tests/ExampleBuilderPattern.java"));
        TestUtils.checkIsEmptyReport(new BuilderPattern(builderProvider, "Hero", "Hero$Builder").run());
    }
}
