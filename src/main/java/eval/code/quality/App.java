package eval.code.quality;

import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.FileProvider;
import eval.code.quality.provider.MultipleContentProvider;
import eval.code.quality.provider.StringProvider;
import eval.code.quality.tests.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class App {

    public static void main(String[] args) {
        ContentProvider contentProvider = MultipleContentProvider.fromContentProvider(new FileProvider(new File("assets/manual/ManualTest.java")),
                new StringProvider("String provider", "public class Test__ {\n\n\n\n          public static void test() {}}"));
        List<Test> listTests = new ArrayList<>();
        listTests.add(new BlankLines(contentProvider));
        listTests.add(new Indentation(contentProvider));
        listTests.add(new Naming(contentProvider));
        TestSuite testSuite = new TestSuite(listTests);
        testSuite.runTests();
        System.out.println(testSuite);
    }
}
