package eval.code.quality.provider;

import com.github.javaparser.ast.CompilationUnit;
import eval.code.quality.utils.Lazy;

import java.util.List;

public class CompilationUnitProvider extends ContentProvider {
    private Lazy<String> content;
    private final CompilationUnit compilationUnit;

    public CompilationUnitProvider(CompilationUnit compilationUnit) {
        this.content = new Lazy<>(compilationUnit::toString);
        this.compilationUnit = compilationUnit;
    }

    @Override
    public String getString() {
        return content.get();
    }

    @Override
    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    @Override
    public void addAll(List<ContentProvider> contentProviders) {
        contentProviders.add(this);
    }
}
