package eval.code.quality.tests;

import eval.code.quality.utils.Error;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Override
    public String toString() {
        return "Error(s) reported: " + ((errors.isEmpty()) ? "no error found " : errors) + System.lineSeparator() + "Warning(s) reported: "
                + ((warnings.isEmpty()) ? "no warning found " : warnings);
    }
}
