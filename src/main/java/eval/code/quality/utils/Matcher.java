package eval.code.quality.utils;

public abstract class Matcher<T> {
    public abstract boolean matches(T actual);
    public abstract void describeMismatch(T actual);
}
