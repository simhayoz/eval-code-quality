package eval.code.quality.provider;

import com.github.javaparser.ast.CompilationUnit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a content provider.
 */
public abstract class ContentProvider implements Iterable<ContentProvider> {

    public abstract String getString();

    public abstract CompilationUnit getCompilationUnit();

    public abstract void addAll(List<ContentProvider> contentProviders);

    @Override
    public Iterator<ContentProvider> iterator() {
        List<ContentProvider> list = new ArrayList<>();
        addAll(list);
        return list.iterator();
    }
}
