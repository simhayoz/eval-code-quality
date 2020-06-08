package eval.code.quality.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LazyTest {
    private int numberOfCompute = 0;

    @Test void nullSupplierThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new Lazy<>(null));
    }

    @Test void canCreateAndComputeLaterValue() {
        Lazy<String> lazy = new Lazy<>(() -> {
            ++numberOfCompute;
            return "done";
        });
        assertEquals(numberOfCompute, 0);
        assertEquals(lazy.get(), "done");
        assertEquals(numberOfCompute, 1);
        assertEquals(lazy.get(), "done");
        assertEquals(numberOfCompute, 1);
    }
}
