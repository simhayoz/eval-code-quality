package eval.code.quality.provider;

import com.github.javaparser.ast.CompilationUnit;
import eval.code.quality.utils.Preconditions;

import java.util.Arrays;
import java.util.List;

/**
 * Content provider represented by multiple {@code ContentProvider}.
 */
public class MultipleContentProvider extends ContentProvider {
    private final List<ContentProvider> contentProviders;

    /**
     * Create a new {@code MultipleContentProvider}.
     *
     * @param contentProviders the list of content provider
     */
    public MultipleContentProvider(List<ContentProvider> contentProviders) {
        Preconditions.checkArg(contentProviders != null, "The list of ContentProvider cannot null");
        this.contentProviders = contentProviders;
    }

    /**
     * Create a new {@code MultipleContentProvider}.
     *
     * @param contentProviders the different content provider
     * @return the new {@code MultipleContentProvider}
     */
    public static MultipleContentProvider fromContentProvider(ContentProvider... contentProviders) {
        return new MultipleContentProvider(Arrays.asList(contentProviders));
    }

    @Override
    public String getString() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompilationUnit getCompilationUnit() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return "Multiple ContentProvider";
    }

    @Override
    public void addAll(List<ContentProvider> contentProviders) {
        for (ContentProvider contentProvider : this.contentProviders) {
            contentProvider.addAll(contentProviders);
        }
    }
}
