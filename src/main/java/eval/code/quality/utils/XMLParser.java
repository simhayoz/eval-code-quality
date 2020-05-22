package eval.code.quality.utils;

import eval.code.quality.checks.Check;
import eval.code.quality.checks.TestSuite;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Collections;

public class XMLParser {

    public static void parse(TestSuite testSuite, File file) throws ParserConfigurationException, TransformerException {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        document.appendChild(testSuite.getXMLElement(document));

        Transformer tr = TransformerFactory.newInstance().newTransformer();
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.setOutputProperty(OutputKeys.METHOD, "xml");
        tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tr.transform(new DOMSource(document), new StreamResult(file));
    }

    public static void parse(Check check, File file) throws TransformerException, ParserConfigurationException {
        parse(new TestSuite(Collections.singletonList(check)), file);
    }
}
