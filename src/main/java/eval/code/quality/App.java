package eval.code.quality;

import eval.code.quality.provider.*;
import eval.code.quality.tests.*;
import eval.code.quality.tests.pattern.SingletonPattern;
import eval.code.quality.utils.Matcher;
import eval.code.quality.utils.booleanExpr.AndExpr;
import eval.code.quality.utils.booleanExpr.BooleanExpr;
import eval.code.quality.utils.booleanExpr.NamedBoolean;
import eval.code.quality.utils.booleanExpr.OrExpr;

import java.io.File;

import static eval.code.quality.utils.booleanExpr.BooleanExpr.*;

public class App {

    public static void main(String[] args) {
//        ContentProvider contentProvider = MultipleContentProvider.fromContentProvider(new FileProvider(new File("assets/manual/ManualTest.java")),
//                new StringProvider("String provider", "public class Test__ {\n\n\n\n          public static void test() {}}"));
        ContentProvider contentProvider = new FileProvider(new File("assets/manual/ManualTest.java"));
//        ContentProvider contentProvider = new DirectoryProvider("src/main/java");
        TestSuite testSuite = new TestSuite();
        testSuite.add(new BlankLines(contentProvider));
        testSuite.add(new Indentation(contentProvider));
        testSuite.add(new Naming(contentProvider));
        testSuite.add(new BracketMatching(contentProvider));
        testSuite.runTests();
        System.out.println(testSuite);
        DesignPattern.enforce(contentProvider.getCompilationUnit().getType(0).asClassOrInterfaceDeclaration(), new SingletonPattern());
//        BooleanExpr booleanExpr = and(or(expr(true, "true"), expr(false, "false")), not(expr(true, "will be false")));
////                new AndExpr(new OrExpr(new NamedBoolean(false, "mismatch"), new NamedBoolean(false, "mismatch2")), new NamedBoolean(true, "no mismatch"));
//        if(!booleanExpr.evaluate()) {
//            System.out.println(booleanExpr.describeMismatch());
//        }
    }
}
