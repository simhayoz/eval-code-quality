package Tests;

import java.util.List;
import Tools.Range;

public abstract class Test {

    static String NAME;

    public List<Tools.Range> runTest() {
        System.out.println("------------- Starting test: " + NAME + " -------------");
        List<Range> result = t();
        System.out.println("-------------   End test: " + NAME + "    -------------\n");
        return result;
    }

    abstract List<Range> t();

    public void printLine(String l) {
        System.out.println("> " + l);
    }
}