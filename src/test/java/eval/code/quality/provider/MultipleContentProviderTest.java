package eval.code.quality.provider;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MultipleContentProviderTest {
    @Test
    void nullInputThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new MultipleContentProvider(null));
    }

    @Test void getStringFromProviderThrowsUnsupportedException() {
        assertThrows(UnsupportedOperationException.class, () -> new MultipleContentProvider(new ArrayList<>()).getString());
    }

    @Test void getCompilationUnitFromProviderThrowsUnsupportedException() {
        assertThrows(UnsupportedOperationException.class, () -> new MultipleContentProvider(new ArrayList<>()).getCompilationUnit());
    }

    @Test void canGetNameFromProvider() {
        assertThat(new MultipleContentProvider(new ArrayList<>()).getName(), equalTo("Multiple ContentProvider"));
    }

    @Test void canAddAllToListOfProvider() {
        MultipleContentProvider multipleContentProvider = new MultipleContentProvider(new ArrayList<>());
        List<ContentProvider> list = new ArrayList<>();
        multipleContentProvider.addAll(list);
        assertThat(list, is(empty()));
        StringProvider stringProvider = new StringProvider("test", "for test");
        FileProvider fileProvider = new FileProvider(new File("assets/tests/FileProvider.java"));
        multipleContentProvider = MultipleContentProvider.fromContentProvider(stringProvider, fileProvider);
        multipleContentProvider.addAll(list);
        assertThat(list, Matchers.<Collection<ContentProvider>>allOf(hasItem(is(stringProvider)), hasItem(is(fileProvider)), hasSize(2)));
    }
}
