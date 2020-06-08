package eval.code.quality.utils;

import java.util.function.Supplier;

public class ThrowingSupplierWrapper {

    public static <T> Supplier<T> throwingSupplierWrapper(ThrowingSupplier<T, Exception> throwingConsumer) {
        return () -> {
            try {
                return throwingConsumer.apply();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }

}
