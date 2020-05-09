package eval.code.quality.tests;

import eval.code.quality.provider.ContentProvider;
import eval.code.quality.tests.pattern.BuilderPatternTest;
import eval.code.quality.tests.pattern.SingletonPatternTest;
import eval.code.quality.tests.pattern.VisitorPatternTest;
import eval.code.quality.utils.Context;

import java.util.List;

public abstract class DesignPatternTest extends Test {

    private final ContentProvider contentProvider;
    protected Context context;

    public DesignPatternTest(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    @Override
    protected void test() {
        if(!enforce(contentProvider)) {
            describeMismatch();
        }
    }

    protected abstract boolean enforce(ContentProvider contentProvider);

    protected abstract void describeMismatch();

    protected String addChevrons(String stringContent) {
        return "<" + stringContent + ">";
    }

    public static DesignPatternTest isSingletonPattern(ContentProvider contentProvider, String className) {
        return new SingletonPatternTest(contentProvider, className);
    }

    public static DesignPatternTest isBuilderPattern(ContentProvider contentProvider, String productName, String builderName) {
        return new BuilderPatternTest(contentProvider, productName, builderName);
    }

    public static DesignPatternTest isVisitorPattern(ContentProvider contentProvider, String parentName, List<String> childrenName, String visitorName) {
        return new VisitorPatternTest(contentProvider, parentName, childrenName, visitorName);
    }
}
