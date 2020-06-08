package eval.code.quality.utils.parameters;

import eval.code.quality.provider.ContentProvider;
import org.apache.commons.cli.CommandLine;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class NameParameterTest {

    @Test
    void canGetNameFromCmd() {
        CommandLine cmd = Mockito.mock(CommandLine.class);
        when(cmd.hasOption("name")).thenReturn(true);
        when(cmd.getOptionValue("name")).thenReturn("Name");
        assertThat(NameParameter.getInstance().getValue(cmd, new JSONObject()), is("Name"));
    }

    @Test void canGetNameFromJson() {
        CommandLine cmd = Mockito.mock(CommandLine.class);
        when(cmd.hasOption("name")).thenReturn(false);
        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        when(jsonObject.has("name")).thenReturn(true);
        when(jsonObject.getString("name")).thenReturn("Name");
        assertThat(NameParameter.getInstance().getValue(cmd, jsonObject), is("Name"));
    }

    @Test void definedTwiceAddWarning() {
        CommandLine cmd = Mockito.mock(CommandLine.class);
        when(cmd.hasOption("name")).thenReturn(true);
        when(cmd.getOptionValue("name")).thenReturn("Name");
        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        when(jsonObject.has("name")).thenReturn(true);
        NameParameter nameParameter = Mockito.spy(NameParameter.class);
        doNothing().when(nameParameter).addDefinedTwice(anyString());
        nameParameter.getValue(cmd, jsonObject);
        verify(nameParameter).addDefinedTwice(eq("name"));
    }

    @Test void notDefinedThrowsError() {
        CommandLine cmd = Mockito.mock(CommandLine.class);
        when(cmd.hasOption("name")).thenReturn(false);
        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        when(jsonObject.has("name")).thenReturn(false);
        NameParameter nameParameter = Mockito.spy(NameParameter.class);
        doNothing().when(nameParameter).throwErrorJSON(anyString());
        nameParameter.getValue(cmd, jsonObject);
        verify(nameParameter).throwErrorJSON(eq("name not found"));
    }
}
