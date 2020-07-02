package eval.code.quality.utils;

import java.util.function.Supplier;

/**
 * Represents a lazy evaluated value.
 *
 * @param <T> the type of the lazy evaluated value
 */
public class Lazy<T> {
    private T value;
    private final Supplier<T> sup;
    private boolean wasComputed;

    /**
     * Create a new {@code Lazy<T>}.
     *
     * @param sup the function to calculate only if needed
     */
    public Lazy(Supplier<T> sup) {
        Preconditions.checkArg(sup != null, "The supplier cannot be null");
        this.value = null;
        this.sup = sup;
        this.wasComputed = false;
    }

    /**
     * Get the value, or compute it.
     *
     * @return the value
     */
    public T get() {
        if (!wasComputed) {
            value = sup.get();
            wasComputed = true;
        }
        return value;
    }
}
