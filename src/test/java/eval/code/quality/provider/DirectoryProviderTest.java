package eval.code.quality.provider;

import eval.code.quality.utils.DirectoryNotFoundError;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DirectoryProviderTest {
    
    @Test void nullOrNonExistingPathThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new DirectoryProvider(null));
        assertThrows(DirectoryNotFoundError.class, () -> new DirectoryProvider("non/existing/path"));
    }

    @Test void canCreateFromSimpleDirectory() {
        ContentProvider contentProvider = new DirectoryProvider("assets/");
        List<ContentProvider> list = new ArrayList<>();
        contentProvider.addAll(list);
        assertThat(list.stream().map(ContentProvider::getName).collect(Collectors.toList()),
                hasItems("assets/tests/EmptyFile.java",
                        "assets/tests/FileProvider.java",
                        "assets/tests/FileToString.java"));
    }

    @Test void canGetName() {
        ContentProvider contentProvider = new DirectoryProvider("assets/");
        assertThat(contentProvider.getName(), is("directory provider: 'assets/'"));
    }

    @Test void tryingToListFileFails() {
        assertThrows(DirectoryNotFoundError.class, () -> new DirectoryProvider("assets/tests/FileProvider.java"));
    }
}
