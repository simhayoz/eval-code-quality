package eval.code.quality.tests;

import com.github.javaparser.ast.CompilationUnit;
import eval.code.quality.provider.ContentProvider;

public class Indentation extends CompilationUnitTest {

    public Indentation(ContentProvider contentProvider) {
        super(contentProvider);
    }

    @Override
    protected void testFor(String content, CompilationUnit compilationUnit) {

    }

    @Override
    protected String getName() {
        return "indentation";
    }
}
