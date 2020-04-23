package eval.code.quality;

import eval.code.quality.provider.*;
import eval.code.quality.tests.*;

import java.io.File;

public class App {

    public static void main(String[] args) {
        ContentProvider contentProvider = MultipleContentProvider.fromContentProvider(new FileProvider(new File("assets/manual/ManualTest.java")),
                new StringProvider("String provider", "public class Test__ {\n\n\n\n          public static void test() {}}"));
//        ContentProvider contentProvider = new DirectoryProvider("src/main/java");
        TestSuite testSuite = new TestSuite();
        testSuite.add(new BlankLines(contentProvider));
        testSuite.add(new Indentation(contentProvider));
        testSuite.add(new Naming(contentProvider));
        testSuite.add(new BracketMatching(contentProvider));
        testSuite.runTests();
        System.out.println(testSuite);
    }
}
