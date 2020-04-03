package eval.code.quality.tests;

import com.github.javaparser.ast.CompilationUnit;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.SCUTuple;

/**
 * Represents all test that needs the {@code CompilationUnit}.
 */
public abstract class CompilationUnitTest extends Test {
    protected final ContentProvider contentProvider;

    public CompilationUnitTest(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    @Override
    protected void test() {
        for (SCUTuple tuple : contentProvider) {
            testFor(tuple.getString(), tuple.getCompilationUnit());
        }
        afterTests();
    }

    /**
     * Run test for current content and {@code CompilationUnit}.
     *
     * @param content         the content
     * @param compilationUnit the CompilationUnit
     */
    protected abstract void testFor(String content, CompilationUnit compilationUnit);

    /**
     * Method called after having done every tests.
     */
    protected void afterTests() { }
}
