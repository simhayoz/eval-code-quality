package eval.code.quality.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface XMLParsable<T> {

    Element getXMLElement(Document document);

    T getFromXML(Element xmlElement);
}
