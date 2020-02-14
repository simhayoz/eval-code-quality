package Tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SFile {

    public static String stringFromPath(String file_path) throws FileNotFoundException {
        return stringFromFile(new File(file_path));
    }

    public static String stringFromFile(File file) throws FileNotFoundException {
        StringBuilder s = new StringBuilder();
        Scanner scanner = new Scanner(file);
        s.append(scanner.nextLine());
        while (scanner.hasNextLine()) {
            s.append("\n");
            s.append(scanner.nextLine());
        }
        scanner.close();
        return s.toString();
    }
}