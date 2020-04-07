package eval.code.quality.provider;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentProviderTest {

    @Test void canGetContentProviderIteratorForMultipleElement() {
        StringProvider s1 = new StringProvider("test", "test");
        StringProvider s2 = new StringProvider("test2", "test2");
        StringProvider s3 = new StringProvider("test3", "test3");
        MultipleContentProvider multipleContentProvider = MultipleContentProvider.fromContentProvider(s1, s2, s3);
        Iterator<ContentProvider> iterator = multipleContentProvider.iterator();
        assertTrue(iterator.hasNext());
        assertThat(iterator.next(), equalTo(s1));
        assertTrue(iterator.hasNext());
        assertThat(iterator.next(), equalTo(s2));
        assertTrue(iterator.hasNext());
        assertThat(iterator.next(), equalTo(s3));
        assertFalse(iterator.hasNext());
    }
}
