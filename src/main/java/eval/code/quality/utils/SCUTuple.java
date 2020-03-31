package eval.code.quality.utils;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.util.Optional;

public class SCUTuple {
    private final String content;
    private Optional<CompilationUnit> cuHelper;

    public SCUTuple(String content) {
        this.content = content;
        this.cuHelper = Optional.empty();
    }

    public String getString() {
        return content;
    }

    public CompilationUnit getCompilationUnit() {
        if(cuHelper.isEmpty()) {
            cuHelper = Optional.of(StaticJavaParser.parse(content));
        }
        return cuHelper.get();
    }
}
