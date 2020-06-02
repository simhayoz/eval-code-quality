package eval.code.quality.provider;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import eval.code.quality.utils.FileToString;
import eval.code.quality.utils.Lazy;
import eval.code.quality.utils.Preconditions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static eval.code.quality.utils.ThrowingSupplierWrapper.throwingSupplierWrapper;

/**
 * Content provider represented by a {@code File}.
 */
public class FileProvider extends ContentProvider {
    private final File file;
    private Lazy<String> content;
    private Lazy<CompilationUnit> compilationUnit;

    /**
     * Create a new {@code FileProvider}.
     *
     * @param file the file
     */
    public FileProvider(File file) {
        Preconditions.checkArg(file != null, "File should not be null");
        Preconditions.checkArg(file.exists(), "File does not exist");
        this.file = file;
        this.content = new Lazy<>(throwingSupplierWrapper(() -> FileToString.fromFile(file)));
        this.compilationUnit = new Lazy<>(throwingSupplierWrapper(() -> StaticJavaParser.parse(file)));
    }

    @Override
    public String getString() {
        return content.get();
    }

    @Override
    public String getName() {
        return file.getPath();
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
