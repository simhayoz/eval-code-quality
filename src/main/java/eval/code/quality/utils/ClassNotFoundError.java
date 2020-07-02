package eval.code.quality.utils;

/**
 * Error to be thrown when a class is not found in the content providers.
 */
public class ClassNotFoundError extends RuntimeException {

    public ClassNotFoundError(String className) {
        super("\"" + className + "\"");
    }
}
