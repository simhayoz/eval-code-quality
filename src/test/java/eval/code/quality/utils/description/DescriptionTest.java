package eval.code.quality.utils.description;

import eval.code.quality.position.SinglePosition;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DescriptionTest {

    @Test void canGetXmlElement() throws ParserConfigurationException {
        Description description = new Description(Collections.singletonList(new PositionDescription(new SinglePosition(1, 2), new Descriptor().setExpected("expectedText"))), new Descriptor().addToDescription("descr"));
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element element = description.getXMLElement(document);
        assertThat(element.getTagName(), is("description"));
        assertThat(element.getElementsByTagName("positionDescription").getLength(), is(1));
        assertThat(element.getElementsByTagName("position").getLength(), is(1));
        assertThat(element.getElementsByTagName("position").item(0).getAttributes().getNamedItem("line").getTextContent(), is("1"));
        assertThat(element.getElementsByTagName("position").item(0).getAttributes().getNamedItem("col").getTextContent(), is("2"));
        assertThat(element.getElementsByTagName("descriptor").getLength(), is(2));
        assertThat(element.getElementsByTagName("expected").getLength(), is(1));
    }
}
