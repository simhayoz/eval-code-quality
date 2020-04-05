package eval.code.quality.tests;

import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.Context;

/**
 * Represents all test that needs the {@code CompilationUnit}.
 */
public abstract class CompilationUnitTest extends Test {
    protected final Context context;

    public CompilationUnitTest(Context context) {
        this.context = new Context(context);
    }

    @Override
    protected void test() {
        while(context.hasNextProvider()) {
            testFor(context.nextProvider());
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
