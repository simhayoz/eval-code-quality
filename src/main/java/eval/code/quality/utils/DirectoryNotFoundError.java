package eval.code.quality.utils;

public class DirectoryNotFoundError extends RuntimeException {
    public DirectoryNotFoundError(String message) {
        super(message);
    }
}
