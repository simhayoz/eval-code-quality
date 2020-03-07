package eval.code.tests;

import java.util.ArrayList;
import java.util.List;

import eval.code.tools.pos.ReportPosition;

/**
 * Report containing the different errors and warnings
 */
public class Report {

    private final List<ReportPosition> errors = new ArrayList<>();
    private final List<ReportPosition> warnings = new ArrayList<>();

    public void addError(ReportPosition error) {
        errors.add(error);
    }

    public void addWarning(ReportPosition warning) {
        warnings.add(warning);
    }

    public List<ReportPosition> getErrors() {
        return errors;
    }

    public List<ReportPosition> getWarnings() {
        return warnings;
    }

    public void clear() {
        errors.clear();
        warnings.clear();
    }

    @Override
    public String toString() {
        return "Error(s) reported: " + errors + "\nWarning(s) reported: " + warnings;
    }
}