package eval.code.quality.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Utils method for getting the {@code String} from a {@code File}
 */
public class FileToString {

    /**
     * Get the content of the file.
     *
     * @param file the file to get the content from
     * @return the content of the file
     * @throws FileNotFoundException if no file where found
     */
    public static String fromFile(File file) throws FileNotFoundException {
        StringBuilder s = new StringBuilder();
        Scanner scanner = new Scanner(file);
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
