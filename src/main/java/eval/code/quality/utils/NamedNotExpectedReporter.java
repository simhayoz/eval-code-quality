package eval.code.quality.utils;

import eval.code.quality.position.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NamedNotExpectedReporter<T> extends NotExpectedReporter<T> {
    private final String name;

    public NamedNotExpectedReporter(String name) {
        this.name = name;
    }

    @Override
    public List<ReportPosition> reportNotExpected(Map<T, List<Position>> map, List<T> expected, List<T> notExpected) {
        List<ReportPosition> errors = new ArrayList<>();
        List<ReportPosition> prevErrors = super.reportNotExpected(map, expected, notExpected);
        prevErrors.forEach(error -> errors.add(ReportPosition.at(error.position, name + ": " + error.description)));
        return errors;
    }
}
