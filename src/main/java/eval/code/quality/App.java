package eval.code.quality;

import eval.code.quality.provider.ContentProvider;
import eval.code.quality.provider.FileProvider;
import eval.code.quality.provider.MultipleContentProvider;
import eval.code.quality.tests.BlankLines;
import eval.code.quality.tests.Indentation;
import eval.code.quality.tests.Naming;
import eval.code.quality.tests.Test;

import java.io.File;

public class App {

    public static void main(String[] args) {
        ContentProvider contentProvider = MultipleContentProvider.fromContentProvider(new FileProvider(new File("assets/tests/ManualTest.java")));
        Test test = new BlankLines(contentProvider);
        test.run();
        System.out.println(test);
        Test test2 = new Indentation(contentProvider);
        test2.run();
        System.out.println(test2);
        Test test3 = new Naming(contentProvider);
        test3.run();
        System.out.println(test3);
//        ContentProvider cp1 = new StringProvider("public class Test {}");
//        ContentProvider cp2 = new FileProvider(new File("assets/tests/ManualTest.java"));
//        ContentProvider cp3 = new StringProvider("public class Test2 {}");
//        ContentProvider contentProviders = MultipleContentProvider.fromContentProvider(cp1, cp2, cp3);
//        for(ContentProvider c: contentProviders) {
//            System.out.println(c.getCompilationUnit());
//        }
    }
}
