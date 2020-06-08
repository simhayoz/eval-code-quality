package eval.code.quality.utils.description;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DescriptorTest {

    @Test void canGetXmlElement() throws ParserConfigurationException {
        Descriptor descriptor = new Descriptor();
        descriptor.addToDescription("first");
        descriptor.addToDescription("second");
        descriptor.setWas("wasText");
        descriptor.setExpected("expectedText");
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element element = descriptor.getXMLElement(document);
        assertThat(element.getTagName(), is("descriptor"));
        assertThat(element.getElementsByTagName("description").getLength(), is(1));
        assertThat(element.getElementsByTagName("description").item(0).getTextContent(), is("firstsecond"));
        assertThat(element.getElementsByTagName("was").getLength(), is(1));
        assertThat(element.getElementsByTagName("was").item(0).getTextContent(), is("wasText"));
        assertThat(element.getElementsByTagName("expected").getLength(), is(1));
        assertThat(element.getElementsByTagName("expected").item(0).getTextContent(), is("expectedText"));
    }
}
