package eval.code.quality.position;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class NamePositionTest {

    @Test void nullInputThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new NamePosition(null, new SinglePosition(0)));
        assertThrows(IllegalArgumentException.class, () -> new NamePosition("name", null));
    }

    @Test void equalsWithOtherNamePosition() {
        NamePosition namePosition = new NamePosition("name", new SinglePosition(2, 2));
        NamePosition otherNamePosition = new NamePosition("name", new SinglePosition(2, 2));
        NamePosition notEqualNamePosition = new NamePosition("name", new SinglePosition(3, 4));
        NamePosition notEqualNamePosition2 = new NamePosition("notSameName", new SinglePosition(3, 4));
        assertEquals(namePosition, namePosition);
        assertEquals(namePosition, otherNamePosition);
        assertNotEquals(namePosition, null);
        assertNotEquals(namePosition, new SinglePosition(2, 2));
        assertNotEquals(namePosition, notEqualNamePosition);
        assertNotEquals(namePosition, notEqualNamePosition2);
    }

    @Test void toStringWorksForSimplePosition() {
        NamePosition namePosition = new NamePosition("name", new SinglePosition(2, 2));
        assertEquals("name (line 2,col 2)", namePosition.toString());
    }

    @Test void canParseSimplePosition() throws ParserConfigurationException {
        NamePosition namePosition = new NamePosition("name_pos", new SinglePosition(4, 2));
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element element = namePosition.getXMLElement(document);
        assertThat(element.getTagName(), is("namedPosition"));
        assertThat(element.getAttributes().getNamedItem("name").getNodeValue(), is("name_pos"));
        assertThat(element.getElementsByTagName("position").getLength(), is(1));
        assertThat(element.getElementsByTagName("position").item(0).getAttributes().getLength(), is(2));
        assertThat(element.getElementsByTagName("position").item(0).getAttributes().getNamedItem("line").getNodeValue(), is("4"));
        assertThat(element.getElementsByTagName("position").item(0).getAttributes().getNamedItem("col").getNodeValue(), is("2"));
    }
}
