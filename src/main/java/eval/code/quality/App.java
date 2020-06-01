package eval.code.quality;

import eval.code.quality.utils.ArgParser;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class App {

    public static void main(String[] args) throws TransformerException, ParserConfigurationException {
        ArgParser.getInstance().parse(args).run();
    }
}
