package eval.code.quality.utils;

import eval.code.quality.position.MultiplePosition;
import eval.code.quality.position.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotExpectedReporter<T> {

    public void doOnNotExpected(Map<T, List<Position>> map, List<T> expected, List<T> notExpected) {
        // Empty default method
    }

    public List<ReportPosition> reportNotExpected(Map<T, List<Position>> map, List<T> expected, List<T> notExpected) {
        List<ReportPosition> reports = new ArrayList<>();
        for (T element : notExpected) {
            if(!map.get(element).isEmpty()) {
                if (map.get(element).size() > 1) {
                    MultiplePosition positions = new MultiplePosition();
                    map.get(element).forEach(positions::add);
                    reports.add(ReportPosition.at(positions, expected.size() == 1 ? expected.get(0).toString() : expected.toString(), element.toString()));
                } else {
                    reports.add(ReportPosition.at(map.get(element).get(0), expected.size() == 1 ? expected.get(0).toString() : expected.toString(), element.toString()));
                }
            }
        }
        return reports;
    }

}
