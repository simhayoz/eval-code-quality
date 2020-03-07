package eval.code.tools;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;

class SFileTest {

    @Test void notKnownFileThrowsFileNotFoundOrNull() {
        assertThrows(FileNotFoundException.class, () -> {SFile.stringFromPath("unknown_path/to_unknown_file.java");});
        assertThrows(FileNotFoundException.class, () -> {SFile.stringFromFile(new File("unknown_path/to_unknown_file.java"));});
        assertThrows(NullPointerException.class, () -> {SFile.stringFromFile(null);});
    }

    @Test void emptyFileProduceEmptyString() throws FileNotFoundException {
        File f = new File("assets/tests/emptyFile.java");
        assertThat(SFile.stringFromFile(f), equalTo(""));
    }

    @Test void testForMultipleLineFile() throws FileNotFoundException {
        File f = new File("assets/tests/MultiLineTest.txt");
        String s_file = SFile.stringFromFile(f);
        String p_file = SFile.stringFromPath("assets/tests/MultiLineTest.txt");
        assertThat(p_file, equalTo(s_file));
        String[] split = s_file.split("\\n");
        String[] expected = {"test", "on", "multiple", "line"};
        assertThat(split, equalTo(expected));
        
    }
    
}