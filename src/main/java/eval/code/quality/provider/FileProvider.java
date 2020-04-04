package eval.code.quality.provider;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import eval.code.quality.utils.FileToString;
import eval.code.quality.utils.Lazy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class FileProvider extends ContentProvider {
    private final File file;
    private Lazy<String> content;
    private Lazy<CompilationUnit> compilationUnit;

    public FileProvider(File file) {
        this.file = file;
        this.content = new Lazy<>(() -> {
            try {
                return FileToString.fromFile(file);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("File not found");
            }
        });
        this.compilationUnit = new Lazy<>(() -> {
            try {
                return StaticJavaParser.parse(file);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("File not found");
            }
        });
    }

    @Override
    public String getString() {
        return content.get();
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
