package eval.code.quality.tests;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import eval.code.quality.tests.pattern.BuilderPattern;
import eval.code.quality.tests.pattern.SingletonPattern;
import eval.code.quality.utils.Matcher;
import eval.code.quality.utils.Tuple;

public class DesignPattern {

    public static <T> void enforce(T element, Matcher<T> matcher) {
        if(!matcher.matches(element)) {
            matcher.describeMismatch(element);
        }
    }

    public static Matcher<ClassOrInterfaceDeclaration> isSingletonPattern() {
        return new SingletonPattern();
    }

    public static Matcher<Tuple<ClassOrInterfaceDeclaration, ClassOrInterfaceDeclaration>> isBuilderPattern() {
        return new BuilderPattern();
    }
}
