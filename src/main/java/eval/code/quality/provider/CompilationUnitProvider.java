package eval.code.quality.provider;

import com.github.javaparser.ast.CompilationUnit;
import eval.code.quality.utils.Lazy;
import eval.code.quality.utils.Preconditions;

import java.util.List;

/**
 * Content provider represented by a {@code CompilationUnit}.
 */
public class CompilationUnitProvider extends ContentProvider {
    private final String name;
    private final Lazy<String> content;
    private final CompilationUnit compilationUnit;

    /**
     * Create a new {@code CompilationUnitProvider}.
     * @param name the name of the content provider
     * @param compilationUnit the {@code CompilationUnit}
     */
    public CompilationUnitProvider(String name, CompilationUnit compilationUnit) {
        Preconditions.checkArg(compilationUnit != null, "CompilationUnit can not be null");
        Preconditions.checkArg(name != null, "CompilationUnit name can not be null");
        this.name = name;
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
    public String getName() {
        return name;
    }

    @Override
    public void addAll(List<ContentProvider> contentProviders) {
        contentProviders.add(this);
    }
}
