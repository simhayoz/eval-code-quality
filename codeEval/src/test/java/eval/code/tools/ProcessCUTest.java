package eval.code.tools;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.jdt.core.dom.CompilationUnit;

class ProcessCUTest {

    @Test void noFileThrowsFileNotFoundOrNull() {
        assertThrows(FileNotFoundException.class, () -> {ProcessCU.fromPath("unknown_path/to_unknown_file.java");});
        assertThrows(FileNotFoundException.class, () -> {ProcessCU.fromFile(new File("unknown_path/to_unknown_file.java"));});
        assertThrows(NullPointerException.class, () -> {ProcessCU.fromFile(null);});
    }

    @Test void stringAndFileProduceSameCU() throws FileNotFoundException {
        String s = "public class SmallFile {public static void main(String[] args) {System.out.println(\"Hello World!\");}}";
        CompilationUnit cu1 = ProcessCU.fromPath("assets/tests/SmallFile.java").getCU();
        CompilationUnit cu2 = ProcessCU.fromFile(new File("assets/tests/SmallFile.java")).getCU();
        CompilationUnit cu3 = ProcessCU.fromString(s).getCU();
        // Cannot use direct equals as it is only implemented on ASTNode
        // as "==" on Object:
        assertThat(cu1.toString(), equalTo(cu2.toString()));
        assertThat(cu1.toString(), equalTo(cu3.toString()));
    }

    @Test void emptyFileReturnEmptyCU() {
        assertThat(ProcessCU.fromString("").getCU().getLength(), equalTo(0));
    }
}