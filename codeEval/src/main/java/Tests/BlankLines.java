package Tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Tools.Position;
import Tools.Range;
import Tools.SFile;

public class BlankLines extends Test {

    private final String content;

    private BlankLines(String content) {
        this.content = content;
        NAME = "blank lines";
    }

    public static BlankLines fromFile(File f) throws FileNotFoundException {
        return new BlankLines(SFile.stringFromFile(f));
    }

    public static BlankLines fromString(String s) {
        return new BlankLines(s);
    }

    @Override
    List<Range> t() {
        Scanner scanner = new Scanner(content);
        List<Range> ranges = new ArrayList<>();
        int count_empty_line = scanner.nextLine().trim().isEmpty() ? 1 : 0;
        int line = 1;
        boolean has_m_blank_line = false;
        while (scanner.hasNextLine()) {
            if(scanner.nextLine().trim().isEmpty()) {
                ++count_empty_line;
            } else {
                if(count_empty_line > 1) {
                    int start_pos = line-count_empty_line+1;
                    printLine(count_empty_line + " empty lines from line " + start_pos + " to " + line);
                    has_m_blank_line = true;
                    ranges.add(new Range(new Position(start_pos, 0), new Position(line, 0)));
                }
                count_empty_line = 0;
            }
            ++line;
        }
        if(!has_m_blank_line) {
            printLine("There is no multiple empty line back to back");
        }
        scanner.close();
        return ranges;
    }

}