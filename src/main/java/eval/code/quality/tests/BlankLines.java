package eval.code.quality.tests;

import eval.code.quality.position.Position;
import eval.code.quality.position.Range;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.ReportPosition;

import java.util.Scanner;

public class BlankLines extends Test {
    private final ContentProvider content;

    public BlankLines(ContentProvider content) {
        this.content = content;
    }

    @Override
    protected void test() {
        for(ContentProvider c: content) {
            testFor(c);
        }
    }

    private void testFor(ContentProvider contentProvider) {
        Scanner scanner = new Scanner(contentProvider.getString());
        if (scanner.hasNextLine()) {
            int count_empty_line = scanner.nextLine().trim().isEmpty() ? 1 : 0;
            int line = 1;
            while (scanner.hasNextLine()) {
                if (scanner.nextLine().trim().isEmpty()) {
                    ++count_empty_line;
                } else {
                    if (count_empty_line > 1) {
                        int start_pos = line - count_empty_line + 1;
                        Position range = new Range(start_pos, line);
                        addError(ReportPosition.at(range, count_empty_line + " empty lines in a row"));
                    }
                    count_empty_line = 0;
                }
                ++line;
            }
        }
        scanner.close();
    }

    @Override
    protected String getName() {
        return "blank lines";
    }
}
