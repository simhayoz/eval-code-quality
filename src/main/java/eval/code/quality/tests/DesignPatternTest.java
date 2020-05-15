package eval.code.quality.tests;

import eval.code.quality.provider.ContentProvider;
import eval.code.quality.tests.pattern.BuilderPatternTest;
import eval.code.quality.tests.pattern.SingletonPatternTest;
import eval.code.quality.tests.pattern.VisitorPatternTest;
import eval.code.quality.utils.Context;
import eval.code.quality.utils.StringError;
import eval.code.quality.utils.description.DescriptionBuilder;
import eval.code.quality.utils.evaluator.BooleanEvaluator;

import java.util.Arrays;
import java.util.List;

public abstract class DesignPatternTest extends Test {

    private final ContentProvider contentProvider;
    protected Context context;

    public DesignPatternTest(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    @Override
    protected void test() {
        try {
            BooleanEvaluator booleanEvaluator = getEvaluator(contentProvider);
            booleanEvaluator.reportMismatches(this);
        } catch (ClassNotFoundException e) {
            addError(new DescriptionBuilder().addToDescription("Class not found: " + e.getMessage()).build());
        }
    }

    protected abstract BooleanEvaluator getEvaluator(ContentProvider contentProvider) throws ClassNotFoundException;

    protected String addChevrons(String stringContent) {
        return "<" + stringContent + ">";
    }

    protected String getSimpleName(String className) {
        String[] names = className.split("\\$");
        return names[names.length-1];
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
