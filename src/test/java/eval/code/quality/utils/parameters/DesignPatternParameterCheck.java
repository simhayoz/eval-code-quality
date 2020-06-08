package eval.code.quality.utils.parameters;

import eval.code.quality.checks.Check;
import eval.code.quality.provider.MultipleContentProvider;
import org.apache.commons.cli.CommandLine;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

public class DesignPatternParameterCheck {

    @Test void canAddSimpleSingletonPattern() {
        JSONObject jsonObject = mockJSON(true, false, false);
        when(jsonObject.getString("singleton")).thenReturn("NameOfSingleton");
        assertThat(getChecksName(jsonObject), containsInAnyOrder("singleton pattern for class <NameOfSingleton>"));
    }

    @Test void canAddSimpleBuilderPattern() {
        JSONObject jsonObject = mockJSON(false, true, false);
        when(jsonObject.getJSONObject("builder")).thenReturn(jsonObject);
        when(jsonObject.has("product")).thenReturn(true);
        when(jsonObject.has("builder")).thenReturn(true);
        when(jsonObject.getString("product")).thenReturn("NameOfProduct");
        when(jsonObject.getString("builder")).thenReturn("NameOfBuilder");
        assertThat(getChecksName(jsonObject), containsInAnyOrder("builder pattern for product <NameOfProduct> and builder <NameOfBuilder>"));
    }

    @Test void canAddSimpleVisitorPattern() {
        JSONObject jsonObject = mockJSON(false, false, true);
        when(jsonObject.getJSONObject("visitor")).thenReturn(jsonObject);
        when(jsonObject.has("visitor")).thenReturn(true);
        when(jsonObject.has("parent")).thenReturn(true);
        when(jsonObject.has("children")).thenReturn(true);
        when(jsonObject.getString("visitor")).thenReturn("NameOfVisitor");
        when(jsonObject.getString("parent")).thenReturn("NameOfParent");
        JSONArray jsonArray = Mockito.mock(JSONArray.class);
        when(jsonObject.getJSONArray("children")).thenReturn(jsonArray);
        List<Object> listReturn = new ArrayList<>();
        listReturn.add("child1");
        listReturn.add("child2");
        listReturn.add("child3");
        when(jsonArray.toList()).thenReturn(listReturn);
        assertThat(getChecksName(jsonObject), containsInAnyOrder("visitor pattern for parent <NameOfParent>, children <[child1, child2, child3]> and visitor <NameOfVisitor>"));
    }

    private JSONObject mockJSON(boolean singleton, boolean builder, boolean visitor) {
        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        when(jsonObject.has("designPattern")).thenReturn(true);
        when(jsonObject.getJSONObject("designPattern")).thenReturn(jsonObject);
        when(jsonObject.has("singleton")).thenReturn(singleton);
        when(jsonObject.has("builder")).thenReturn(builder);
        when(jsonObject.has("visitor")).thenReturn(visitor);
        return jsonObject;
    }

    private List<String> getChecksName(JSONObject jsonObject) {
        return new DesignPatternParameter(new MultipleContentProvider(new ArrayList<>()))
                .getValue(Mockito.mock(CommandLine.class), jsonObject)
                .stream()
                .map(Check::getName)
                .collect(Collectors.toList());
    }
}
