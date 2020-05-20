package eval.code.quality.checks;

import eval.code.quality.utils.XMLParsable;
import eval.code.quality.utils.description.Description;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Report containing the different errors and warnings.
 */
public class Report implements XMLParsable<Report> {

    private final List<Description> errors = new ArrayList<>();
    private final List<Description> warnings = new ArrayList<>();

    /**
     * Add an error to the report.
     *
     * @param error the error to add
     */
    public void addError(Description error) {
        errors.add(error);
    }

    /**
     * Add a warning to the report.
     *
     * @param warning the warning to add
     */
    public void addWarning(Description warning) {
        warnings.add(warning);
    }

    /**
     * Get the unmodifiable list of errors.
     *
     * @return the unmodifiable list of errors
     */
    public List<Description> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Get the unmodifiable list of warnings.
     *
     * @return the unmodifiable list of warnings
     */
    public List<Description> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    private <T> String prettyPrintList(List<T> list) {
        return list.stream().map(e -> e.toString().indent(1)).collect(Collectors.joining());
    }

    @Override
    public String toString() {
        return "Error(s) reported: " + System.lineSeparator() + ((errors.isEmpty()) ? "no error found ".indent(1) : prettyPrintList(errors)) + System.lineSeparator() + "Warning(s) reported: " + System.lineSeparator() + ((warnings.isEmpty()) ? "no warning found ".indent(1) : prettyPrintList(warnings));
    }

    @Override
    public Element getXMLElement(Document document) {
        Element root = document.createElement("report");
        Element error = document.createElement("errors");
        root.setAttribute("number_errors", Integer.toString(errors.size()));
        root.setAttribute("number_warnings", Integer.toString(warnings.size()));
        for(Description description : errors) {
            error.appendChild(description.getXMLElement(document));
        }
        root.appendChild(error);
        Element warning = document.createElement("warnings");
        for(Description description : warnings) {
            warning.appendChild(description.getXMLElement(document));
        }
        root.appendChild(warning);
        return root;
    }

    @Override
    public Report getFromXML(Element xmlElement) {
        return null;
    }
}
