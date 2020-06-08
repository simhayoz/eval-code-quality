package eval.code.quality.utils;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Exception> {
    T apply() throws E;
}
