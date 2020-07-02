package eval.code.quality.utils;

public class FileNotFoundError extends RuntimeException {
    public FileNotFoundError(String message) {
        super(message);
    }
}
