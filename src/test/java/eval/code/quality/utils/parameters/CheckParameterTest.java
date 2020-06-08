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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class CheckParameterTest {

    @Test void canAddAllChecksWithAllCommandLine() {
        CommandLine cmd = Mockito.mock(CommandLine.class);
        String[] cmdReturn = new String[]{"blank lines", "all", "braces"};
        when(cmd.getOptionValues("check")).thenReturn(cmdReturn);
        assertThat(getChecksName(cmd, new JSONObject()), containsInAnyOrder(CheckParameter.allPossibleCheck.keySet().toArray()));
    }

    @Test void canAddAllChecksWithAllJSON() {
        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        when(jsonObject.has("check")).thenReturn(true);
        JSONArray jsonArray = Mockito.mock(JSONArray.class);
        when(jsonObject.getJSONArray("check")).thenReturn(jsonArray);
        List<Object> listReturn = new ArrayList<>();
        listReturn.add("indentation");
        listReturn.add("braces");
        listReturn.add("all");
        when(jsonArray.toList()).thenReturn(listReturn);
        CommandLine cmd = Mockito.mock(CommandLine.class);
        when(cmd.getOptionValues("check")).thenReturn(null);
        assertThat(getChecksName(cmd, jsonObject), containsInAnyOrder(CheckParameter.allPossibleCheck.keySet().toArray()));
    }

    @Test void canAddSomeChecksFromBoth() {
        CommandLine cmd = Mockito.mock(CommandLine.class);
        when(cmd.getOptionValues("check")).thenReturn(new String[]{"braces"});
        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        when(jsonObject.has("check")).thenReturn(true);
        JSONArray jsonArray = Mockito.mock(JSONArray.class);
        when(jsonObject.getJSONArray("check")).thenReturn(jsonArray);
        List<Object> listReturn = new ArrayList<>();
        listReturn.add("indentation");
        when(jsonArray.toList()).thenReturn(listReturn);
        assertThat(getChecksName(cmd, jsonObject), containsInAnyOrder("indentation", "braces"));
    }

    @Test void whenNoneDefinedAddAll() {
        CommandLine cmd = Mockito.mock(CommandLine.class);
        when(cmd.getOptionValues("check")).thenReturn(null);
        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        when(jsonObject.has("check")).thenReturn(false);
        assertThat(getChecksName(cmd, jsonObject), containsInAnyOrder(CheckParameter.allPossibleCheck.keySet().toArray()));
    }

    private List<String> getChecksName(CommandLine cmd, JSONObject jsonObject) {
        return new CheckParameter(new MultipleContentProvider(new ArrayList<>())).getValue(cmd, jsonObject)
                .stream()
                .map(Check::getName)
                .collect(Collectors.toList());
    }
}
