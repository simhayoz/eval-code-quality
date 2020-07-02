package eval.code.quality.position;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class MultiplePositionTest {

    @Test void nullListThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new MultiplePosition(null));
    }

    @Test void multiplePositionWorkForSimplePosition() {
        MultiplePosition m = new MultiplePosition();
        assertThat(m.getPositions(), is(empty()));
        m.add(new SinglePosition(0, 0));
        assertThat(m.getPositions(), Matchers.<Collection<Position>>allOf(hasItem(new SinglePosition(0, 0)), hasSize(1)));
        List<Position> list = new ArrayList<>();
        list.add(new SinglePosition(1, 2));
        list.add(new SinglePosition(3, 4));
        MultiplePosition multiplePosition = new MultiplePosition(list);
        assertThat(multiplePosition.getPositions(), Matchers.<Collection<Position>>allOf(hasItem(new SinglePosition(1, 2)),
                hasItem(new SinglePosition(3, 4)), hasSize(2)));
    }

    @Test void equalsWorksForSimpleList() {
        MultiplePosition m = new MultiplePosition();
        m.add(new SinglePosition(1, 2));
        m.add(new SinglePosition(3, 4));
        List<Position> list = new ArrayList<>();
        list.add(new SinglePosition(1, 2));
        list.add(new SinglePosition(3, 4));
        MultiplePosition m2 = new MultiplePosition(list);
        MultiplePosition m3 = new MultiplePosition();
        m3.add(new SinglePosition(1, 2));
        assertEquals(m, m);
        assertEquals(m, m2);
        assertNotEquals(m, m3);
        assertNotEquals(m, null);
        assertNotEquals(m, new Object());
    }

    @Test void canGetXmlElement() throws ParserConfigurationException {
        MultiplePosition m = new MultiplePosition();
        m.add(new SinglePosition(0));
        m.add(new SinglePosition(2, 3));
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element element = m.getXMLElement(document);
        assertThat(element.getTagName(), is("multiplePositions"));
        assertThat(element.getElementsByTagName("position").getLength(), is(2));
        assertThat(element.getElementsByTagName("position").item(0).getAttributes().getLength(), is(1));
        assertThat(element.getElementsByTagName("position").item(0).getAttributes().item(0).getNodeValue(), is("0"));
        assertThat(element.getElementsByTagName("position").item(1).getAttributes().getLength(), is(2));
        assertThat(element.getElementsByTagName("position").item(1).getAttributes().getNamedItem("line").getNodeValue(), is("2"));
        assertThat(element.getElementsByTagName("position").item(1).getAttributes().getNamedItem("col").getNodeValue(), is("3"));
    }
}
