package eval.code.quality;

import eval.code.quality.provider.*;
import eval.code.quality.checks.*;
import eval.code.quality.utils.XMLParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

import static eval.code.quality.checks.DesignPattern.*;

public class App {

    public static void main(String[] args) {
//        ContentProvider contentProvider = MultipleContentProvider.fromContentProvider(new FileProvider(new File("assets/manual/ManualTest.java")),
//                new StringProvider("String provider", "public class Test__ {\n\n\n\n          public static void test() {}}"));
        ContentProvider contentProvider = new FileProvider(new File("assets/manual/ManualTest.java"));
//        ContentProvider contentProvider = new DirectoryProvider("assets/manual/main");
        TestSuite testSuite = new TestSuite();
        testSuite.add(new BlankLines(contentProvider));
        testSuite.add(new Indentation(contentProvider));
        testSuite.add(new Naming(contentProvider));
        testSuite.add(new Braces(contentProvider));
        ContentProvider singletonProvider = new FileProvider(new File("assets/tests/ExampleSingletonPattern.java"));
        testSuite.add(isSingletonPattern(singletonProvider, "IvoryTower"));
        testSuite.add(isSingletonPattern(contentProvider, "Details"));
        ContentProvider builderProvider = new FileProvider(new File("assets/tests/ExampleBuilderPattern.java"));
        testSuite.add(isBuilderPattern(builderProvider, "Hero", "Hero$Builder"));
        ContentProvider visitorProvider = new DirectoryProvider("assets/tests/ExampleVisitor");
        List<String> childrenName = List.of("Book", "Fruit");
        testSuite.add(isVisitorPattern(visitorProvider, "Item", childrenName, "Visitor"));
        testSuite.runChecks();
        System.out.println(testSuite);

        try {
            XMLParser.parse(testSuite, new File("test.xml"));
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }

    }
}
