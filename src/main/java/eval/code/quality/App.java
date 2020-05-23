package eval.code.quality;

import eval.code.quality.utils.ArgParser;
import eval.code.quality.utils.JSONParser;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

public class App {

    public static void main(String[] args) throws NoSuchMethodException, ParserConfigurationException, TransformerException, InstantiationException, IllegalAccessException, InvocationTargetException, FileNotFoundException {
        System.out.println(ArgParser.getInstance().parse(args));
    }
}
