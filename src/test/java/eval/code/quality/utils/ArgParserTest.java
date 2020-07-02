package eval.code.quality.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.hamcrest.core.StringContains;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ArgParserTest {

    private HelpFormatter mockFormatter;
    private ArgParser argParser;

    @BeforeEach void initMock() {
        mockFormatter = Mockito.mock(HelpFormatter.class);
        argParser = Mockito.spy(ArgParser.class);
        when(argParser.getFormatter()).thenReturn(mockFormatter);
        doNothing().when(argParser).exitSystem(anyInt());
    }

    @Test void withHelperArgShowHelpMessage() {
        String[] args = new String[] {"-h"};
        argParser.parse(args);
        verify(mockFormatter).printHelp(eq("run"), any(Options.class));
        args = new String[] {"-help"};
        argParser.parse(args);
        verify(mockFormatter, times(2)).printHelp(eq("run"), any(Options.class));
        verify(argParser, times(2)).exitSystem(0);
    }

    @Test void canGetJSONHelpMessage() {
        String[] args = new String[] {"-help-json"};
        argParser.parse(args);
        verify(argParser).printJSONHelp();
    }

    @Test void canParseCommandLineArg() {
        String[] args = new String[] {"-n", "nameOfTest", "-c", "\"blank lines\""};
        TestRunner testRunner = argParser.parse(args);
        assertThat(testRunner.toString(), StringContains.containsString("name='nameOfTest'"));
        assertThat(testRunner.toString(), StringContains.containsString("checkToRun=[blank lines]"));
    }

    @Test void canGetJsonConfig() throws FileNotFoundException {
        String filePath = "assets/tests/jsonConfig.json";
        try(FileWriter jsonWriter = new FileWriter(filePath)) {
            jsonWriter.write("{");
            jsonWriter.write(" \"name\": \"test\"");
            jsonWriter.write("}");
        } catch (IOException e) {
            fail("Could not write into test file");
        }
        CommandLine cmd = Mockito.mock(CommandLine.class);
        when(cmd.hasOption("json")).thenReturn(true);
        when(cmd.getOptionValue("json")).thenReturn(filePath);
        JSONObject result = argParser.getJSONParam(cmd);
        assertTrue(result.has("name"));
        assertThat(result.getString("name"), is("test"));
        File file = new File(filePath);
        if(!file.delete()) {
            fail("Could not delete test file");
        }
    }

    @Test void canNotGetOtherFormatThanJson() throws FileNotFoundException {
        CommandLine cmd = Mockito.mock(CommandLine.class);
        when(cmd.hasOption("json")).thenReturn(true);
        when(cmd.getOptionValue("json")).thenReturn("wrong.ext");
        doNothing().when(argParser).exitSystem(anyInt());
        argParser.getJSONParam(cmd);
        verify(mockFormatter).printHelp(eq("run"), any(Options.class));
        verify(argParser).exitSystem(1);
    }
}
