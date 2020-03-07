package eval.code.tests;

import java.util.Scanner;

import eval.code.tools.pos.Position;

/**
 * Check for multiple blank line in a row a file
 * 
 * @author Simon Hayoz
 */
public class BlankLines extends Test {

    private final String content;

    public BlankLines(String content) {
        this.content = content;
        NAME = "blank lines";
    }

    @Override
    protected void test() {
        Scanner scanner = new Scanner(content);
        if (!scanner.hasNextLine()) {
            scanner.close();
        } else {
            int count_empty_line = scanner.nextLine().trim().isEmpty() ? 1 : 0;
            int line = 1;
            while (scanner.hasNextLine()) {
                if (scanner.nextLine().trim().isEmpty()) {
                    ++count_empty_line;
                } else {
                    if (count_empty_line > 1) {
                        int start_pos = line - count_empty_line + 1;
                        Position range = Position.setRangeOrSinglePos(Position.setPos(start_pos, 0), Position.setPos(line, 0));
                        addError(range, count_empty_line + " empty lines in a row");
                    }
                    count_empty_line = 0;
                }
                ++line;
            }
            scanner.close();
        }
    }

}