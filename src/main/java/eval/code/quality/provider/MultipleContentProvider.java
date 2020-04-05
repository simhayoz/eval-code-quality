package eval.code.quality.provider;

import com.github.javaparser.ast.CompilationUnit;

import java.util.Arrays;
import java.util.List;

public class MultipleContentProvider extends ContentProvider {
    private final List<ContentProvider> contentProviders;

    public MultipleContentProvider(List<ContentProvider> contentProviders) {
        this.contentProviders = contentProviders;
    }

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
        for(ContentProvider contentProvider: this.contentProviders) {
            contentProvider.addAll(contentProviders);
        }
    }
}
