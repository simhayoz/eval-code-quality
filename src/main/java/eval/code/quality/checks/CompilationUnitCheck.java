package eval.code.quality.checks;

import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.Context;

/**
 * Represents all checks that needs the {@code CompilationUnit}.
 */
public abstract class CompilationUnitCheck extends Check {
    private final ContentProvider contentProvider;
    protected Context context;

    public CompilationUnitCheck(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    @Override
    protected void check() {
        context = new Context(contentProvider);
        while(context.hasNext()) {
            checkFor(context.next());
        }
        afterChecks();
    }

    /**
     * Run check for current content and {@code CompilationUnit}.
     *
     * @param contentProvider the current ContentProvider
     */
    protected abstract void checkFor(ContentProvider contentProvider);

    /**
     * Method called after having done every checks.
     */
    protected void afterChecks() { }
}
