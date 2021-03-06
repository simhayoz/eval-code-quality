package eval.code.quality.checks;

import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.Context;
import eval.code.quality.utils.description.DescriptionBuilder;
import eval.code.quality.utils.description.Descriptor;

import java.util.Scanner;

/**
 * Check for multiple blank lines in a row.
 */
public class BlankLines extends Check {
    private final ContentProvider contentProvider;
    private Context context;

    /**
     * Create a new {@code BlankLines} with a context.
     *
     * @param contentProvider the contentProvider
     */
    public BlankLines(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    @Override
    protected void check() {
        context = new Context(contentProvider);
        while (context.hasNext()) {
            checkFor(context.next());
        }
    }

    private void checkFor(ContentProvider contentProvider) {
        Scanner scanner = new Scanner(contentProvider.getString());
        if (scanner.hasNextLine()) {
            iterateOverScanner(scanner);
        }
        scanner.close();
    }

    private void iterateOverScanner(Scanner scanner) {
        int countEmptyLine = scanner.nextLine().trim().isEmpty() ? 1 : 0;
        int line = 1;
        while (scanner.hasNextLine()) {
            if (scanner.nextLine().trim().isEmpty()) {
                ++countEmptyLine;
            } else {
                if (countEmptyLine > 1) {
                    addError(new DescriptionBuilder()
                            .addPosition(context.getRange(line - countEmptyLine + 1, line),
                                    new Descriptor().addToDescription(countEmptyLine + " empty lines in a row")));
                }
                countEmptyLine = 0;
            }
            ++line;
        }
    }

    @Override
    public String getName() {
        return "blank lines";
    }
}
