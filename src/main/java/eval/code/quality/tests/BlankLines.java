package eval.code.quality.tests;

import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.Context;
import eval.code.quality.utils.ReportPosition;

import java.util.Scanner;

/**
 * Check for multiple blank lines in a row.
 */
public class BlankLines extends Test {
    private final Context context;

    /**
     * Create a new {@code BlankLines} with a context.
     *
     * @param context the context
     */
    public BlankLines(Context context) {
        this.context = new Context(context);
    }

    @Override
    protected void test() {
        while (context.hasNext()) {
            testFor(context.next());
        }
    }

    private void testFor(ContentProvider contentProvider) {
        Scanner scanner = new Scanner(contentProvider.getString());
        if (scanner.hasNextLine()) {
            iterateOverScanner(scanner);
        }
        scanner.close();
    }

    private void iterateOverScanner(Scanner scanner) {
        int count_empty_line = scanner.nextLine().trim().isEmpty() ? 1 : 0;
        int line = 1;
        while (scanner.hasNextLine()) {
            if (scanner.nextLine().trim().isEmpty()) {
                ++count_empty_line;
            } else {
                if (count_empty_line > 1) {
                    addError(ReportPosition.at(context.getRange(line - count_empty_line + 1, line), count_empty_line + " empty lines in a row"));
                }
                count_empty_line = 0;
            }
            ++line;
        }
    }

    @Override
    protected String getName() {
        return "blank lines";
    }
}
