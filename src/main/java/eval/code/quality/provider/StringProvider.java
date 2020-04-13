package eval.code.quality.provider;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import eval.code.quality.utils.Lazy;
import eval.code.quality.utils.Preconditions;

import java.util.List;

/**
 * Content provider represented by a String.
 */
public class StringProvider extends ContentProvider {
    private final String name;
    private final String content;
    private Lazy<CompilationUnit> compilationUnit;

    /**
     * Create a new {@code StringProvider}.
     *
     * @param name    the name of the content provider
     * @param content the String content
     */
    public StringProvider(String name, String content) {
        Preconditions.checkArg(name != null, "String name cannot be null");
        Preconditions.checkArg(content != null, "String cannot be null");
        this.name = name;
        this.content = content;
        this.compilationUnit = new Lazy<>(() -> StaticJavaParser.parse(content));
    }

    @Override
    public String getString() {
        return content;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CompilationUnit getCompilationUnit() {
        return compilationUnit.get();
    }

    @Override
    public void addAll(List<ContentProvider> contentProviders) {
        contentProviders.add(this);
    }
}
