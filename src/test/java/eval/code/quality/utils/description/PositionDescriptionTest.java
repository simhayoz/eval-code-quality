package eval.code.quality.utils.description;

import eval.code.quality.position.SinglePosition;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;

public class PositionDescriptionTest {

    @Test void nullInputThrowsIllegal() {
        assertThrows(IllegalArgumentException.class, () -> new PositionDescription(null, new Descriptor()));
    }

    @Test void positionDescriptionEquals() {
        PositionDescription positionDescription = new PositionDescription(new SinglePosition(1, 2), new Descriptor().addToDescription("descr"));
        PositionDescription positionDescription2 = new PositionDescription(new SinglePosition(1, 2), new Descriptor().addToDescription("descr1234"));
        PositionDescription positionDescription3 = new PositionDescription(new SinglePosition(3, 2), new Descriptor().addToDescription("descr"));
        assertEquals(positionDescription, positionDescription);
        assertEquals(positionDescription, positionDescription2);
        assertNotEquals(positionDescription, positionDescription3);
        assertNotEquals(positionDescription, null);
        assertNotEquals(positionDescription, new Object());
    }

    @Test void canGetXmlElement() throws ParserConfigurationException {
        PositionDescription positionDescription = new PositionDescription(new SinglePosition(1, 2), new Descriptor().addToDescription("descr"));
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element element = positionDescription.getXMLElement(document);
        assertThat(element.getTagName(), is("positionDescription"));
        assertThat(element.getElementsByTagName("descriptor").getLength(), is(1));
        assertThat(element.getElementsByTagName("descriptor").item(0).getFirstChild().getTextContent(), is("descr"));
        assertThat(element.getElementsByTagName("position").getLength(), is(1));
        assertThat(element.getElementsByTagName("position").item(0).getAttributes().getNamedItem("line").getTextContent(), is("1"));
        assertThat(element.getElementsByTagName("position").item(0).getAttributes().getNamedItem("col").getTextContent(), is("2"));
        positionDescription = new PositionDescription(new SinglePosition(1, 2), null);
        document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        element = positionDescription.getXMLElement(document);
        assertThat(element.getTagName(), is("positionDescription"));
        assertThat(element.getElementsByTagName("descriptor").getLength(), is(0));
        assertThat(element.getElementsByTagName("position").getLength(), is(1));
        assertThat(element.getElementsByTagName("position").item(0).getAttributes().getNamedItem("line").getTextContent(), is("1"));
        assertThat(element.getElementsByTagName("position").item(0).getAttributes().getNamedItem("col").getTextContent(), is("2"));

    }

}
