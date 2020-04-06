package eval.code.quality;

import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.FileProvider;
import eval.code.quality.provider.MultipleContentProvider;
import eval.code.quality.provider.StringProvider;
import eval.code.quality.tests.*;
import eval.code.quality.utils.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class App {

    public static void main(String[] args) {
        ContentProvider contentProvider = MultipleContentProvider.fromContentProvider(new FileProvider(new File("assets/tests/ManualTest.java")),
                new StringProvider("String provider", "public class Test__ {\n\n\n\n          public static void test() {}}"));
//        ContentProvider contentProvider = new FileProvider(new File("assets/tests/test.java"));
        List<Test> listTests = new ArrayList<>();
        Context context = new Context(contentProvider);
        listTests.add(new BlankLines(context));
        listTests.add(new Indentation(context));
        listTests.add(new Naming(context));
        TestSuite testSuite = new TestSuite(listTests);
        testSuite.runTests();
        System.out.println(testSuite);
//        ContentProvider cp1 = new StringProvider("TEST1", "public class Test {}");
//        ContentProvider cp2 = new FileProvider(new File("assets/tests/ManualTest.java"));
//        ContentProvider cp3 = new StringProvider("TEST3", "public class Test2 {}");
//        ContentProvider contentProviders = MultipleContentProvider.fromContentProvider(cp1, cp2, cp3);
//        Context context = new Context(contentProviders);
//        while(context.hasNextProvider()) {
//            context.nextProvider();
//            System.out.println(context.setPos(1));
//        }
    }
}
