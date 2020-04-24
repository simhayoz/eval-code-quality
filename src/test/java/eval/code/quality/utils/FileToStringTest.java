package eval.code.quality.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileToStringTest {

    @Test void nullFileThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> FileToString.fromFile(null));
    }

    @Test void canGetStringFromFile() throws IOException {
        String content = FileToString.fromFile(new File("assets/tests/FileToString.java"));
        assertEquals(content, "public class Test {\n\n    public void test() {}\n}");
        content = FileToString.fromFile(new File("assets/tests/EmptyFile.java"));
        assertEquals(content, "");
    }
}
