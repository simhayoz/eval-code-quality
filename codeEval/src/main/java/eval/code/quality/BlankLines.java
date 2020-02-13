package eval.code.quality;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BlankLines {

    private final String content;

    private BlankLines(String content) {
        this.content = content;
    }

    public static BlankLines fromFile(File f) throws FileNotFoundException {
        StringBuilder s = new StringBuilder();
        Scanner scanner = new Scanner(f);
        s.append(scanner.nextLine());
        while (scanner.hasNextLine()) {
            s.append("\n");
            s.append(scanner.nextLine());
        }
        scanner.close();
        return new BlankLines(s.toString());
    }

    public static BlankLines fromString(String s) {
        return new BlankLines(s);
    }

    public List<Range> hasMultipleBlankLine() {
        Scanner scanner = new Scanner(content);
        List<Range> ranges = new ArrayList<>();
        int count_empty_line = scanner.nextLine().trim().isEmpty() ? 1 : 0;
        int line = 1;
        boolean has_m_blank_line = false;
        System.out.println("------------- Test for multiple empty line Started -------------");
        while (scanner.hasNextLine()) {
            if(scanner.nextLine().trim().isEmpty()) {
                ++count_empty_line;
            } else {
                if(count_empty_line > 1) {
                    int start_pos = line-count_empty_line+1;
                    System.out.println("There is "+count_empty_line+" empty lines from line "+start_pos+" to "+line);
                    has_m_blank_line = true;
                    ranges.add(new Range(new Position(start_pos, 0), new Position(line, 0)));
                }
                count_empty_line = 0;
            }
            ++line;
        }
        if(!has_m_blank_line) {
            System.out.println("There is no multiple empty line back to back");
        }
        System.out.println("............. Test for multiple empty line Ended   .............");
        scanner.close();
        return ranges;
    }

}