package eval.code.quality.utils;

import eval.code.quality.checks.Check;
import eval.code.quality.checks.TestSuite;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.MultipleContentProvider;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class TestRunner {

    private final String name;
    private final boolean shouldPrintToTerminal;
    private final MultipleContentProvider contentProviders;
    private final List<Class<? extends Check>> checkToRun;
    private final File xmlOutput;

    public TestRunner(String name, boolean shouldPrintToTerminal, MultipleContentProvider contentProviders, File xmlOutput, List<Class<? extends Check>> checkToRun) {
        this.name = name;
        this.shouldPrintToTerminal = shouldPrintToTerminal;
        this.contentProviders = contentProviders;
        this.xmlOutput = xmlOutput;
        this.checkToRun = checkToRun;
    }

    public void run() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, TransformerException, ParserConfigurationException {
        TestSuite testSuite = new TestSuite(checkToRun, name, contentProviders);
        testSuite.runChecks();
        if(shouldPrintToTerminal) {
            System.out.println(testSuite);
        }
        if(xmlOutput != null) {
            XMLParser.parse(testSuite, xmlOutput);
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

    public static class Builder {
        private String name;
        private boolean shouldPrintToTerminal;
        private final List<ContentProvider> contentProviders;
        private final Set<Class<? extends Check>> checkToRun;
        private File xmlOutput;

        public Builder(String name, boolean shouldPrintToTerminal) {
            this.name = name;
            this.shouldPrintToTerminal = shouldPrintToTerminal;
            this.contentProviders = new ArrayList<>();
            this.checkToRun = new HashSet<>();
        }

        public String getName() {
            return name;
        }

        public Builder addContentProviders(ContentProvider contentProvider) {
            Preconditions.checkArg(contentProvider != null, "Content provider can not be null");
            contentProviders.add(contentProvider);
            return this;
        }

        public Builder addCheck(Class<? extends Check> check) {
            Preconditions.checkArg(check != null, "Check can not be null");
            checkToRun.add(check);
            return this;
        }

        public Builder clearCheck() {
            checkToRun.clear();
            return this;
        }

        public Builder setXmlOutput(File xmlOutput) {
            this.xmlOutput = xmlOutput;
            return this;
        }

        public TestRunner build() {
            MultipleContentProvider multipleContentProvider = new MultipleContentProvider(contentProviders);
            return new TestRunner(name, shouldPrintToTerminal, multipleContentProvider, xmlOutput, new ArrayList<>(checkToRun));
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Set<Class<? extends Check>> getCheck() {
            return checkToRun;
        }

        public File getXmlOutput() {
            return xmlOutput;
        }
    }
}
