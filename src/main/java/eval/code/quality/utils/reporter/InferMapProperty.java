package eval.code.quality.utils.reporter;

import eval.code.quality.position.Position;
import eval.code.quality.tests.Test;
import eval.code.quality.utils.description.Description;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Helper class for reporting a map of property when there is multiple properties.
 * <p>
 * It report the following error:
 *     <ul>
 *         <li>If the map has multiple max number of element -> reports can not infer "good" property</li>
 *         <li>If the map has element smaller than max number of element -> reports "wrong" property</li>
 *     </ul>
 * </p>
 */
public class InferMapProperty {

    private final Test test;

    /**
     * Create a new {@code InferMapProperty} to test and report error.
     *
     * @param test the test to report error to
     */
    public InferMapProperty(Test test) {
        this.test = test;
    }

    /**
     * Check and report the {@code map}.
     *
     * @param map          the map to report
     * @param shouldReport whether it should report smaller property if it can not infer a unique property
     * @param <T>          the type of the property
     */
    public <T> void checkAndReport(Map<T, List<Position>> map, boolean shouldReport) {
        checkAndReport(map, new ExpectedReporter<>(), new NotExpectedReporter<>(), shouldReport);
    }

    /**
     * Check and report the {@code map}.
     *
     * @param map          the map to report
     * @param name         the name of the tested property
     * @param shouldReport whether it should report smaller property if it can not infer a unique property
     * @param <T>          the type of the property
     */
    public <T> void checkAndReport(Map<T, List<Position>> map, String name, boolean shouldReport) {
        checkAndReport(map, new NamedExpectedReporter<>(name), new NamedNotExpectedReporter<>(name), shouldReport);
    }

    /**
     * Check and report the {@code map}.
     *
     * @param map                 the map to report
     * @param expectedReporter    the expected reporter
     * @param notExpectedReporter the not expected reporter
     * @param shouldReport        whether it should report smaller property if it can not infer a unique property
     * @param <T>                 the type of the property
     */
    public <T> void checkAndReport(Map<T, List<Position>> map, ExpectedReporter<T> expectedReporter, NotExpectedReporter<T> notExpectedReporter, boolean shouldReport) {
        if (map.size() > 1) {
            List<T> goodList = new ArrayList<>();
            List<T> wrongList = new ArrayList<>();
            setGoodAndWrongList(map, goodList, wrongList);
            if (goodList.size() > 1) {
                addIfNotNull(expectedReporter.reportMultipleExpected(map, goodList));
                if (shouldReport) {
                    notExpectedReporter.reportNotExpected(map, goodList, wrongList).forEach(test::addError);
                }
            } else {
                expectedReporter.doOnUniqueExpected(map, goodList.get(0));
                notExpectedReporter.reportNotExpected(map, goodList, wrongList).forEach(test::addError);
                notExpectedReporter.doOnNotExpected(map, goodList, wrongList);
            }
        }
    }

    /**
     * Get the list of good and wrong properties.
     *
     * @param map       the map to separate
     * @param goodList  the list of possible good properties
     * @param wrongList the list of wrong properties
     * @param <T>       the type of the property
     */
    public <T> void setGoodAndWrongList(Map<T, List<Position>> map, List<T> goodList, List<T> wrongList) {
        Map<T, Integer> indentationCount = map.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
        int maxNumberOfElement = Collections.max(indentationCount.values());
        indentationCount.forEach((key, value) -> (value == maxNumberOfElement ? goodList : wrongList).add(key));
    }

    private void addIfNotNull(Description error) {
        if (error != null) {
            test.addError(error);
        }
    }

}
