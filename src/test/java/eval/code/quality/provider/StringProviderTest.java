package eval.code.quality.provider;

import com.github.javaparser.StaticJavaParser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringProviderTest {
    @Test
    void nullInputThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new StringProvider(null, "content"));
        assertThrows(IllegalArgumentException.class, () -> new StringProvider("name", null));
    }

    @Test void canGetStringFromProvider() {
        StringProvider stringProvider = new StringProvider("name", "public class Test {\n}\n");
        assertEquals(stringProvider.getString(), "public class Test {\n}\n");
    }

    @Test void canGetCompilationUnitFromProvider() {
        StringProvider stringProvider = new StringProvider("name", "public class Test {\n}\n");
        assertEquals(stringProvider.getCompilationUnit(), StaticJavaParser.parse("public class Test {\n}\n"));
    }

    @Test void canGetNameFromProvider() {
        StringProvider stringProvider = new StringProvider("testName", "content");
        assertEquals(stringProvider.getName(), "testName");
    }

    @Test void canAddAllToListOfProvider() {
        List<ContentProvider> list = new ArrayList<>();
        StringProvider stringProvider = new StringProvider("name", "content");
        stringProvider.addAll(list);
        assertThat(list, Matchers.<Collection<ContentProvider>>allOf(hasItem(is(stringProvider)), hasSize(1)));
    }
}
