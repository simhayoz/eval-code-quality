package eval.code.quality.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PreconditionsTest {

    @Test void checkArgThrowsIllegalArgumentWhenFalse() {
        assertThrows(IllegalArgumentException.class, () -> Preconditions.checkArg(false));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Preconditions.checkArg(false, "Error message"));
        assertEquals("Error message", exception.getMessage());
    }

    @Test void checkArgWorksWhenTrue() {
        assertDoesNotThrow(() -> Preconditions.checkArg(true));
        assertDoesNotThrow(() -> Preconditions.checkArg(true, "Error message"));
    }
}
