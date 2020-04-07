package eval.code.quality.utils;

/**
 * Utility class for easier checking of arguments.
 */
public final class Preconditions {

    private Preconditions() {}

    /**
     * Check if the argument is true, if not throws an error.
     *
     * @param b   the boolean to check
     * @param log the error message if the boolean is false
     */
    public static void checkArg(boolean b, String log) {
        if (!b) {
            throw new IllegalArgumentException(log);
        }
    }

    /**
     * Check if the argument is true, if not throws an error.
     *
     * @param b the boolean to check
     */
    public static void checkArg(boolean b) {
        checkArg(b, "");
    }
}
