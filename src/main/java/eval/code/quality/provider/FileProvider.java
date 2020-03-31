package eval.code.quality.provider;

import eval.code.quality.utils.FileToString;

import java.io.File;
import java.io.FileNotFoundException;

public class FileProvider extends StringProvider {

    public FileProvider(File file) {
        super(setFile(file));
    }

    public static String setFile(File file) {
        try {
            return FileToString.fromFile(file);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File was not found");
        }
    }
}
