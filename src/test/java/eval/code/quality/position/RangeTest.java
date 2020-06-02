package eval.code.quality.position;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class RangeTest {
    @Test
    void setRangeThrowsErrorForNull() {
        SinglePosition p = new SinglePosition(0, 0);
        assertThrows(IllegalArgumentException.class, () -> {new Range(p, null);});
        assertThrows(IllegalArgumentException.class, () -> {new Range(null, p);});
        assertThrows(IllegalArgumentException.class, () -> {new Range(null, null);});
        assertThrows(IllegalArgumentException.class, () -> {Range.from(null);});
    }

    @Test void setRangeNegativeOrderDoesNotWork() {
        SinglePosition p = new SinglePosition(0, 0);
        SinglePosition p2 = new SinglePosition(5, 8);
        assertThrows(IllegalArgumentException.class, () -> new Range(p2, p));
    }


    @Test void setRangeForSimplePosWorks() {
        SinglePosition p = new SinglePosition(0, 0);
        SinglePosition p2 = new SinglePosition(4, 5);
        Range r = new Range(p, p2);
        assertThat(r.begin.line, equalTo(0));
        assertThat(r.end.line, equalTo(4));
        assertThat(r.begin.column.get(), equalTo(0));
        assertThat(r.end.column.get(), equalTo(5));
    }

    @Test void canCreateFromInt() {
        Range r = new Range(1, 3);
        assertThat(r.begin.line, equalTo(1));
        assertThat(r.end.line, equalTo(3));
        assertThat(r.begin.column, equalTo(Optional.empty()));
        assertThat(r.end.column, equalTo(Optional.empty()));
        r = new Range(1, 3, 5, 7);
        assertThat(r.begin.line, equalTo(1));
        assertThat(r.end.line, equalTo(5));
        assertThat(r.begin.column.get(), equalTo(3));
        assertThat(r.end.column.get(), equalTo(7));
    }

    @Test void toStringProduceRightString() {
        Range r = new Range(1, 3);
        assertThat(r.toString(), equalTo("((line 1) -> (line 3))"));
        r = new Range(1, 3, 5, 7);
        assertThat(r.toString(), equalTo("((line 1,col 3) -> (line 5,col 7))"));
    }

    @Test void equalWorkForSimpleRange() {
        Range r = new Range(1, 3);
        Range r2 = new Range(1, 3);
        Range r3 = new Range(1, 4);
        assertEquals(r, r);
        assertNotEquals(r, null);
        assertNotEquals(r, new Object());
        assertEquals(r, r2);
        assertNotEquals(r, r3);
    }

    @Test void canCreateFromJavaParserPosition() {
        com.github.javaparser.Position begin = new com.github.javaparser.Position(1, 2);
        com.github.javaparser.Position end = new com.github.javaparser.Position(3, 4);
        com.github.javaparser.Range range = new com.github.javaparser.Range(begin, end);
        Range rangeFrom = Range.from(range);
        assertThat(rangeFrom.begin, equalTo(new SinglePosition(1, 2)));
        assertThat(rangeFrom.end, equalTo(new SinglePosition(3, 4)));
    }

    @Test void canGetHashCode() {
        SinglePosition p = new SinglePosition(1, 2);
        SinglePosition p2 = new SinglePosition(4, 5);
        Range r = new Range(p, p2);
        assertThat(r.hashCode(), is(8460));
    }

    @Test void canParseSimpleRange() throws ParserConfigurationException {
        SinglePosition p = new SinglePosition(1, 2);
        SinglePosition p2 = new SinglePosition(4, 5);
        Range r = new Range(p, p2);
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element element = r.getXMLElement(document);
        assertThat(element.getTagName(), is("range"));
        assertThat(element.getElementsByTagName("begin").getLength(), is(1));
        assertThat(element.getElementsByTagName("begin").item(0).getAttributes().getLength(), is(2));
        assertThat(element.getElementsByTagName("begin").item(0).getAttributes().getNamedItem("line").getNodeValue(), is("1"));
        assertThat(element.getElementsByTagName("begin").item(0).getAttributes().getNamedItem("col").getNodeValue(), is("2"));
        assertThat(element.getElementsByTagName("end").getLength(), is(1));
        assertThat(element.getElementsByTagName("end").item(0).getAttributes().getLength(), is(2));
        assertThat(element.getElementsByTagName("end").item(0).getAttributes().getNamedItem("line").getNodeValue(), is("4"));
        assertThat(element.getElementsByTagName("end").item(0).getAttributes().getNamedItem("col").getNodeValue(), is("5"));
    }
}
