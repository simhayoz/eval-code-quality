/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package eval.code;

import java.io.File;
import java.io.FileNotFoundException;

import eval.code.tests.BlankLines;
import eval.code.tests.BracketMatching;
import eval.code.tests.Indentation;
import eval.code.tests.Naming;
import eval.code.tests.Test;
import eval.code.tools.ProcessCU;
import eval.code.tools.SFile;

public class App {

    public static void main(String[] args) {
      try {
        Test b = new BlankLines(SFile.stringFromFile(new File(args[0])));
        b.runTest();
        System.out.println(b);
        ProcessCU pcu = ProcessCU.fromFile(new File(args[0]));
        Test t = new Indentation(pcu.getCU());
        t.runTest();
        System.out.println(t);
        Test naming = new Naming(pcu.getCU());
        naming.runTest();
        System.out.println(naming);
        Test bMatching = new BracketMatching(pcu.getCU());
        bMatching.runTest(true);
        System.out.println(bMatching);
      } catch(FileNotFoundException e) {

      }
    } 
}
