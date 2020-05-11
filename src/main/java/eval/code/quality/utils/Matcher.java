package eval.code.quality.utils;

import com.github.javaparser.ast.CompilationUnit;

public abstract class Matcher {
    public abstract boolean matches(CompilationUnit actual);
    public abstract String describeMismatch(CompilationUnit actual);
}
