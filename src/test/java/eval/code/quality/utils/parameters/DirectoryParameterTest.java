package eval.code.quality.utils.parameters;

import eval.code.quality.provider.ContentProvider;
import org.apache.commons.cli.CommandLine;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

public class DirectoryParameterTest {

    @Test void canGetDirectoryFromCmd() {
        CommandLine cmd = Mockito.mock(CommandLine.class);
        when(cmd.getOptionValues("directory")).thenReturn(new String[] {"src/", "src/main"});
        assertThat(DirectoryParameter.getInstance().getValue(cmd, new JSONObject()).stream().map(ContentProvider::getName).collect(Collectors.toList()), containsInAnyOrder("directory provider: 'src/'", "directory provider: 'src/main'"));
    }

    @Test void canGetDirectoryFromJson() {
        CommandLine cmd = Mockito.mock(CommandLine.class);
        when(cmd.getOptionValues("directory")).thenReturn(null);
        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        when(jsonObject.has("directory")).thenReturn(true);
        JSONArray jsonArray = Mockito.mock(JSONArray.class);
        when(jsonObject.getJSONArray("directory")).thenReturn(jsonArray);
        when(jsonArray.toList()).thenReturn(List.of("src/", "src/main"));
        assertThat(DirectoryParameter.getInstance().getValue(cmd, jsonObject).stream().map(ContentProvider::getName).collect(Collectors.toList()), containsInAnyOrder("directory provider: 'src/'", "directory provider: 'src/main'"));
    }
}
