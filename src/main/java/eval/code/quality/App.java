package eval.code.quality;

import eval.code.quality.provider.*;
import eval.code.quality.checks.*;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.util.List;

import static eval.code.quality.checks.DesignPattern.*;

public class App {

    public static void main(String[] args) throws ParserConfigurationException {
//        ContentProvider contentProvider = MultipleContentProvider.fromContentProvider(new FileProvider(new File("assets/manual/ManualTest.java")),
//                new StringProvider("String provider", "public class Test__ {\n\n\n\n          public static void test() {}}"));
        ContentProvider contentProvider = new FileProvider(new File("assets/manual/ManualTest.java"));
//        ContentProvider contentProvider = new DirectoryProvider("assets/manual/main");
//        TestSuite testSuite = new TestSuite();
//        testSuite.add(new BlankLines(contentProvider));
//        testSuite.add(new Indentation(contentProvider));
//        testSuite.add(new Naming(contentProvider));
//        testSuite.add(new Braces(contentProvider));
//        ContentProvider singletonProvider = new FileProvider(new File("assets/tests/ExampleSingletonPattern.java"));
//        testSuite.add(isSingletonPattern(singletonProvider, "IvoryTower"));
//        testSuite.add(isSingletonPattern(contentProvider, "Details"));
//        ContentProvider builderProvider = new FileProvider(new File("assets/tests/ExampleBuilderPattern.java"));
//        testSuite.add(isBuilderPattern(builderProvider, "Hero", "Hero$Builder"));
//        ContentProvider visitorProvider = new DirectoryProvider("assets/tests/ExampleVisitor");
//        List<String> childrenName = List.of("Book", "Fruit");
//        testSuite.add(isVisitorPattern(visitorProvider, "Item", childrenName, "Visitor"));
//        testSuite.runChecks();
//        System.out.println(testSuite);

//        System.out.println(new BlankLines(contentProvider).getXMLElement());

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        prettyPrint(new Indentation(contentProvider).getXMLElement(document), "    ");
    }

    private static void prettyPrint(Node node, String tab)
    {
        if (node.getNodeType() == Node.TEXT_NODE)
        {
            System.out.print(tab);
            System.out.println(node.getNodeValue());
        }
        else if (node.getNodeType() == Node.ELEMENT_NODE)
        {
            System.out.print(tab);
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < node.getAttributes().getLength(); i++) {
                builder.append(" ").append(node.getAttributes().item(i));
            }
            System.out.println("<" + node.getNodeName() + builder.toString() + ">");
            NodeList kids = node.getChildNodes();
            for (int i = 0; i < kids.getLength(); i++)
            {
                prettyPrint(kids.item(i), tab + "  ");
            }
            System.out.print(tab);
            System.out.println("</" + node.getNodeName() + ">");
        }
    }
}
