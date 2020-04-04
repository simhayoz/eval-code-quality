package eval.code.quality.utils;

import java.util.Optional;
import java.util.function.Supplier;

public class Lazy<T> {
    private T value;
    private final Supplier<T> sup;

    public Lazy(Supplier<T> sup) {
        this.value = null;
        this.sup = sup;
    }

    public T get() {
        if(value == null) {
            value = sup.get();
            if(value == null) {
                throw new NullPointerException();
            }
        }
        return value;
    }
}
