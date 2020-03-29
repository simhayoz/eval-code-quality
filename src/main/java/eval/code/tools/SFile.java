package eval.code.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Tools for getting the string out of a file
 * 
 * @author Simon Hayoz
 */
public class SFile {

    /**
     * Get the content as a String of the file from its path
     * 
     * @param file_path path to the file
     * @return the content of the file
     * @throws FileNotFoundException
     */
    public static String stringFromPath(String file_path) throws FileNotFoundException {
        return stringFromFile(new File(file_path));
    }

    /**
     * Get the content as a String of the file
     * 
     * @param file the file
     * @return the content of the file
     * @throws FileNotFoundException
     */
    public static String stringFromFile(File file) throws FileNotFoundException {
        if (file == null) {
            throw new NullPointerException("File is null");
        }
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