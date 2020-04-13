package eval.code.quality.tests;

import eval.code.quality.utils.Error;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Report containing the different errors and warnings.
 */
public class Report {

    private final List<Error> errors = new ArrayList<>();
    private final List<Error> warnings = new ArrayList<>();

    /**
     * Add an error to the report.
     *
     * @param error the error to add
     */
    public void addError(Error error) {
        errors.add(error);
    }

    /**
     * Add a warning to the report.
     *
     * @param warning the warning to add
     */
    public void addWarning(Error warning) {
        warnings.add(warning);
    }

    /**
     * Get the unmodifiable list of errors.
     *
     * @return the unmodifiable list of errors
     */
    public List<Error> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Get the unmodifiable list of warnings.
     *
     * @return the unmodifiable list of warnings
     */
    public List<Error> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    private String prettyPrintList(List<Error> list) {
        return list.stream().map(e -> e.toString().indent(1)).collect(Collectors.joining());
    }

    @Override
    public String toString() {
        return "Error(s) reported: " + System.lineSeparator() + ((errors.isEmpty()) ? "no error found ".indent(1) : prettyPrintList(errors)) + System.lineSeparator() + "Warning(s) reported: " + System.lineSeparator() + ((warnings.isEmpty()) ? "no warning found ".indent(1) : prettyPrintList(warnings));
    }
}
