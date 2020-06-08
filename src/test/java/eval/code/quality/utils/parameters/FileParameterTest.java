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

public class FileParameterTest {
    @Test
    void canGetFileFromCmd() {
        CommandLine cmd = Mockito.mock(CommandLine.class);
        when(cmd.getOptionValues("file")).thenReturn(new String[] {"assets/tests/EmptyFile.java", "assets/tests/FileProvider.java"});
        assertThat(FileParameter.getInstance().getValue(cmd, new JSONObject()).stream().map(ContentProvider::getName).collect(Collectors.toList()), containsInAnyOrder("assets/tests/EmptyFile.java", "assets/tests/FileProvider.java"));
    }

    @Test void canGetFileFromJson() {
        CommandLine cmd = Mockito.mock(CommandLine.class);
        when(cmd.getOptionValues("file")).thenReturn(null);
        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        when(jsonObject.has("file")).thenReturn(true);
        JSONArray jsonArray = Mockito.mock(JSONArray.class);
        when(jsonObject.getJSONArray("file")).thenReturn(jsonArray);
        when(jsonArray.toList()).thenReturn(List.of("assets/tests/EmptyFile.java", "assets/tests/FileProvider.java"));
        assertThat(FileParameter.getInstance().getValue(cmd, jsonObject).stream().map(ContentProvider::getName).collect(Collectors.toList()), containsInAnyOrder("assets/tests/EmptyFile.java", "assets/tests/FileProvider.java"));
    }
}
