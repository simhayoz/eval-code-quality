package eval.code.quality.utils.parameters;

import org.apache.commons.cli.CommandLine;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class OutputParameterTest {

    @Test
    void canGetOutputFromCmd() {
        CommandLine cmd = Mockito.mock(CommandLine.class);
        when(cmd.hasOption("output")).thenReturn(true);
        when(cmd.getOptionValue("output")).thenReturn("test.xml");
        assertThat(OutputParameter.getInstance().getValue(cmd, new JSONObject()).getName(), is("test.xml"));
    }

    @Test void canGetOutputFromJson() {
        CommandLine cmd = Mockito.mock(CommandLine.class);
        when(cmd.hasOption("output")).thenReturn(false);
        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        when(jsonObject.has("output")).thenReturn(true);
        when(jsonObject.getString("output")).thenReturn("test.xml");
        assertThat(OutputParameter.getInstance().getValue(cmd, jsonObject).getName(), is("test.xml"));
    }

    @Test void definedTwiceAddWarning() {
        CommandLine cmd = Mockito.mock(CommandLine.class);
        when(cmd.hasOption("output")).thenReturn(true);
        when(cmd.getOptionValue("output")).thenReturn("test.xml");
        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        when(jsonObject.has("output")).thenReturn(true);
        OutputParameter outputParameter = Mockito.spy(OutputParameter.class);
        doNothing().when(outputParameter).addDefinedTwice(anyString());
        outputParameter.getValue(cmd, jsonObject);
        verify(outputParameter).addDefinedTwice(eq("output"));
    }

}
