package eval.code.quality.utils;

import eval.code.quality.checks.Check;
import eval.code.quality.checks.TestSuite;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.MultipleContentProvider;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.util.*;

public class TestRunner {

    private final String name;
    private final boolean shouldPrintToTerminal;
    private final MultipleContentProvider contentProviders;
    private final List<Check> checkToRun;
    private final File xmlOutput;

    public TestRunner(String name, boolean shouldPrintToTerminal, MultipleContentProvider contentProviders, File xmlOutput, List<Check> checkToRun) {
        this.name = name;
        this.shouldPrintToTerminal = shouldPrintToTerminal;
        this.contentProviders = contentProviders;
        this.xmlOutput = xmlOutput;
        this.checkToRun = checkToRun;
    }

    public void run() throws TransformerException, ParserConfigurationException {
        TestSuite testSuite = new TestSuite(checkToRun, name);
        testSuite.runChecks();
        if(shouldPrintToTerminal) {
            System.out.println(testSuite);
        }
        if(xmlOutput != null) {
            XMLParser.getInstance().parse(testSuite, xmlOutput);
        }
    }

    @Override
    public String toString() {
        return "TestRunner{" + System.lineSeparator() +
                ("name='" + name + '\'' + System.lineSeparator() +
                ", shouldPrintToTerminal=" + shouldPrintToTerminal + System.lineSeparator() +
                ", contentProviders=" + contentProviders + System.lineSeparator() +
                ", checkToRun=" + checkToRun + System.lineSeparator() +
                ", xmlOutput=" + xmlOutput).indent(4) + System.lineSeparator() +
                '}';
    }
}
