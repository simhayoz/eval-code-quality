package eval.code.quality.tests;

import com.github.javaparser.ast.CompilationUnit;
import eval.code.quality.provider.ContentProvider;

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
        for (ContentProvider current : contentProvider) {
            testFor(current);
        }
        afterTests();
    }

    /**
     * Run test for current content and {@code CompilationUnit}.
     *
     * @param contentProvider the current ContentProvider
     */
    protected abstract void testFor(ContentProvider contentProvider);

    /**
     * Method called after having done every tests.
     */
    protected void afterTests() { }
}
