package eval.code.quality.tests;

import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.Descriptor;
import eval.code.quality.utils.Matcher;

import java.util.List;

public class DesignPattern {

    public static <T> void enforce(T element, Matcher<T> matcher) {
        if(!matcher.matches(element)) {
            matcher.describeMismatch(element);
        }
    }
}
