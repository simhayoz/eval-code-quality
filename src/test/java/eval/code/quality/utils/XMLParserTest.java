package eval.code.quality.utils;

import org.junit.jupiter.api.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class XMLParserTest {

    @Test
    void wrongInputThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> XMLParser.getInstance().parse(null, new File("test.xml")));
        assertThrows(IllegalArgumentException.class, () -> XMLParser.getInstance().parse(document -> document.createElement("test"), null));
    }

    @Test
    void canOutputToXml() throws TransformerException, ParserConfigurationException {
        String filePath = "assets/tests/test.xml";
        XMLParser.getInstance().parse(document -> document.createElement("test"),
                new File(filePath));
        try (Scanner scanner = new Scanner(new FileInputStream(filePath)).useDelimiter("\\A")) {
            String content = scanner.hasNext() ? scanner.next() : "";
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                    "<test/>\n", content);
        } catch (FileNotFoundException e) {
            fail("Could not read file for test");
        }
        File file = new File(filePath);
        if (!file.delete()) {
            fail("Could not delete test file");
        }
    }
}
