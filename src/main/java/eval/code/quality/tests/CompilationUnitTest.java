package eval.code.quality.tests;

import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.Context;

/**
 * Represents all test that needs the {@code CompilationUnit}.
 */
public abstract class CompilationUnitTest extends Test {
    private final ContentProvider contentProvider;
    protected Context context;

    public CompilationUnitTest(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    @Override
    protected void test() {
        context = new Context(contentProvider);
        while(context.hasNext()) {
            testFor(context.next());
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
