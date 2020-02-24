package eval.code.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import eval.code.tools.pos.Position;
import eval.code.tools.pos.SinglePosition;
import eval.code.tools.SFile;

/**
 * Check for multiple blank line in a row a file
 * 
 * @author Simon Hayoz
 */
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
    protected List<Position> test() {
        Scanner scanner = new Scanner(content);
        List<Position> positions = new ArrayList<>();
        int count_empty_line = scanner.nextLine().trim().isEmpty() ? 1 : 0;
        int line = 1;
        boolean has_m_blank_line = false;
        while (scanner.hasNextLine()) {
            if (scanner.nextLine().trim().isEmpty()) {
                ++count_empty_line;
            } else {
                if (count_empty_line > 1) {
                    int start_pos = line - count_empty_line + 1;
                    SinglePosition start = Position.setPos(start_pos, 0);
                    SinglePosition end = Position.setPos(line, 0);
                    Position range = Position.setRangeOrSinglePos(start, end);
                    printError(count_empty_line + " empty lines at " + range);
                    has_m_blank_line = true;
                    positions.add(range);
                }
                count_empty_line = 0;
            }
            ++line;
        }
        if (!has_m_blank_line) {
            printSuccess();
        }
        scanner.close();
        return positions;
    }

}