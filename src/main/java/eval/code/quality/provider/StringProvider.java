package eval.code.quality.provider;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import eval.code.quality.utils.Lazy;
import eval.code.quality.utils.Preconditions;

import java.util.List;

public class StringProvider extends ContentProvider {
    private final String content;
    private Lazy<CompilationUnit> compilationUnit;

    public StringProvider(String content) {
        Preconditions.checkArg(content != null, "String cannot be null");
        this.content = content;
        this.compilationUnit = new Lazy<>(() -> StaticJavaParser.parse(content));
    }

    @Override
    public String getString() {
        return content;
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
