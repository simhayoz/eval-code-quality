package eval.code.quality.provider;

import com.github.javaparser.StaticJavaParser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileProviderTest {

    private final File file = new File("assets/tests/FileProvider.java");

    @Test
    void nullInputThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new FileProvider(null));
        assertThrows(IllegalArgumentException.class, () -> new FileProvider(new File("unknown_path")));
    }

    @Test void canGetStringFromProvider() {
        FileProvider fileProvider = new FileProvider(file);
        assertEquals(fileProvider.getString(), "public class Test {\n\n}");
    }

    @Test void canGetCompilationUnitFromProvider() {
        FileProvider fileProvider = new FileProvider(file);
        assertEquals(fileProvider.getCompilationUnit(), StaticJavaParser.parse("public class Test {}"));
    }

    @Test void canGetNameFromProvider() {
        FileProvider fileProvider = new FileProvider(file);
        assertEquals(fileProvider.getName(), "FileProvider.java");
    }

    @Test void canAddAllToListOfProvider() {
        List<ContentProvider> list = new ArrayList<>();
        FileProvider fileProvider = new FileProvider(file);
        fileProvider.addAll(list);
        assertThat(list, Matchers.<Collection<ContentProvider>>allOf(hasItem(is(fileProvider)), hasSize(1)));
    }
}
