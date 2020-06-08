package eval.code.quality.checks;

import eval.code.quality.checks.pattern.BuilderPattern;
import eval.code.quality.checks.pattern.SingletonPattern;
import eval.code.quality.checks.pattern.VisitorPattern;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.description.DescriptionBuilder;
import eval.code.quality.utils.evaluator.BooleanEvaluator;

import java.util.List;

public abstract class DesignPattern extends Check {

    private final ContentProvider contentProvider;

    public DesignPattern(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    @Override
    protected void check() {
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

    public static DesignPattern isSingletonPattern(ContentProvider contentProvider, String className) {
        return new SingletonPattern(contentProvider, className);
    }

    public static DesignPattern isBuilderPattern(ContentProvider contentProvider, String productName, String builderName) {
        return new BuilderPattern(contentProvider, productName, builderName);
    }

    public static DesignPattern isVisitorPattern(ContentProvider contentProvider, String parentName, List<String> childrenName, String visitorName) {
        return new VisitorPattern(contentProvider, parentName, childrenName, visitorName);
    }
}
