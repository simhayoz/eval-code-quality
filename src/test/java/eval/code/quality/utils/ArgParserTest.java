package eval.code.quality.utils;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ArgParserTest {

    private HelpFormatter mockFormatter;
    private boolean wasCalled;

    @BeforeEach void initMock() {
        mockFormatter = Mockito.mock(HelpFormatter.class);
        wasCalled = false;
    }

    @Test void withHelperArgShowHelpMessage() {
        ArgParser argParser = new FakeArgParser(i -> assertEquals(i, 0));
        String[] args = new String[] {"-h"};
        argParser.parse(args);
        verify(mockFormatter).printHelp(eq("run"), any(Options.class));
        argParser = new FakeArgParser(i -> assertEquals(i, 0));
        args = new String[] {"-help"};
        argParser.parse(args);
        verify(mockFormatter, times(2)).printHelp(eq("run"), any(Options.class));
    }

     @Test void canGetJSONHelpMessage() {
         ArgParser argParser = new FakeArgParser(i -> assertEquals(i, 0)) {
             @Override
             public void printJSONHelp() {
                 super.printJSONHelp();
                 setWasCalled(true);
             }
         };
         String[] args = new String[] {"-help-json"};
         argParser.parse(args);
         assertTrue(wasCalled);
     }

     @Test void canParseCommandLineArg() {
         ArgParser argParser = new FakeArgParser(i -> assertEquals(i, 0));
         String[] args = new String[] {"-n", "nameOfTest", "-c", "\"blank lines\""};
         TestRunner testRunner = argParser.parse(args);
         assertThat(testRunner.toString(), StringContains.containsString("name='nameOfTest'"));
         assertThat(testRunner.toString(), StringContains.containsString("checkToRun=[blank lines]"));
     }

    private class FakeArgParser extends ArgParser {

        private final Consumer<Integer> doOnExit;

        protected FakeArgParser() {
            this(t -> {});
        }

        protected FakeArgParser(Consumer<Integer> doOnExit) {
            this.doOnExit = doOnExit;
        }

        @Override
        protected HelpFormatter getFormatter() {
            return mockFormatter;
        }

        @Override
        protected void exitSystem(int code) {
            doOnExit.accept(code);
        }
    }

    private void setWasCalled(boolean wasCalled) {
        this.wasCalled = wasCalled;
    }
}
