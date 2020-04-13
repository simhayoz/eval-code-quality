package eval.code.quality.provider;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompilationUnitProviderTest {

    private final CompilationUnit compilationUnit = StaticJavaParser.parse("public class Test {}");

    @Test void nullInputThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new CompilationUnitProvider(null, compilationUnit));
        assertThrows(IllegalArgumentException.class, () -> new CompilationUnitProvider("name", null));
    }

    @Test void canGetStringFromProvider() {
        CompilationUnitProvider compilationUnitProvider = new CompilationUnitProvider("name", compilationUnit);
        assertEquals(compilationUnitProvider.getString(), "public class Test {\n}\n");
    }

    @Test void canGetCompilationUnitFromProvider() {
        CompilationUnitProvider compilationUnitProvider = new CompilationUnitProvider("name", compilationUnit);
        assertEquals(compilationUnitProvider.getCompilationUnit(), compilationUnit);
    }

    @Test void canGetNameFromProvider() {
        CompilationUnitProvider compilationUnitProvider = new CompilationUnitProvider("testName", compilationUnit);
        assertEquals(compilationUnitProvider.getName(), "testName");
    }

    @Test void canAddAllToListOfProvider() {
        List<ContentProvider> list = new ArrayList<>();
        CompilationUnitProvider compilationUnitProvider = new CompilationUnitProvider("name", compilationUnit);
        compilationUnitProvider.addAll(list);
        assertThat(list, Matchers.<Collection<ContentProvider>>allOf(hasItem(is(compilationUnitProvider)), hasSize(1)));
    }
}
