package eval.code.quality.provider;

import com.github.javaparser.ast.CompilationUnit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a content provider.
 */
public abstract class ContentProvider implements Iterable<ContentProvider> {

    /**
     * Get the string of the {@code ContentProvider}.
     *
     * @return the string of the {@code ContentProvider}
     */
    public abstract String getString();

    /**
     * Get the {@code CompilationUnit} of the {@code ContentProvider}.
     *
     * @return the {@code CompilationUnit} of the {@code ContentProvider}
     */
    public abstract CompilationUnit getCompilationUnit();

    /**
     * Add all child {@code ContentProvider} to the list of {@code ContentProvider} for easier iteration.
     *
     * @param contentProviders the current list of {@code ContentProvider}
     */
    public abstract void addAll(List<ContentProvider> contentProviders);

    /**
     * Get the name of the {@code ContentProvider}.
     *
     * @return the name of the {@code ContentProvider}
     */
    public abstract String getName();

    @Override
    public Iterator<ContentProvider> iterator() {
        List<ContentProvider> list = new ArrayList<>();
        addAll(list);
        return list.iterator();
    }
}
