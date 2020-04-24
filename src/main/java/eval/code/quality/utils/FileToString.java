package eval.code.quality.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Utils method for getting the {@code String} from a {@code File}.
 */
public final class FileToString {

    private FileToString() {}

    /**
     * Get the content of the file.
     *
     * @param file the file to get the content from
     * @return the content of the file
     * @throws FileNotFoundException if no file where found
     */
    public static String fromFile(File file) throws IOException {
        Preconditions.checkArg(file != null, "Cannot get string from empty file");
        StringBuilder s = new StringBuilder();
        Scanner scanner = new Scanner(file, StandardCharsets.UTF_8);
        if (scanner.hasNextLine()) {
            s.append(scanner.nextLine());
        } else {
            scanner.close();
            return "";
        }
        while (scanner.hasNextLine()) {
            s.append(System.lineSeparator());
            s.append(scanner.nextLine());
        }
        scanner.close();
        return s.toString();
    }
}
