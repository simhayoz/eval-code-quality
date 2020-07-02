package eval.code.quality.utils;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class XMLParser {

    private static final XMLParser INSTANCE = new XMLParser();

    private XMLParser() {
    }

    /**
     * Get {@code XMLParser} instance.
     *
     * @return {@code XMLParser} instance
     */
    public static XMLParser getInstance() {
        return INSTANCE;
    }

    /**
     * Parse the {@code XMLParsable} into an xml file.
     *
     * @param parsable the parsable to parse
     * @param file     the xml file to parse into
     * @throws ParserConfigurationException in case of parsing error
     * @throws TransformerException         in case of parsing error
     */
    public void parse(XMLParsable parsable, File file) throws ParserConfigurationException, TransformerException {
        Preconditions.checkArg(parsable != null, "cannot parse null element");
        Preconditions.checkArg(file != null, "Cannot parse to a null file");
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        document.appendChild(parsable.getXMLElement(document));
        Transformer tr = TransformerFactory.newInstance().newTransformer();
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.setOutputProperty(OutputKeys.METHOD, "xml");
        tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tr.transform(new DOMSource(document), new StreamResult(file));
    }

}
