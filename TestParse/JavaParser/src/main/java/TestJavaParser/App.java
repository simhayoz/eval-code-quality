/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package TestJavaParser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.YamlPrinter;
import com.github.javaparser.printer.XmlPrinter;

import com.github.javaparser.Position;
import com.github.javaparser.Range;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException {
      File f = new File(args[0]);
      CompilationUnit cu = StaticJavaParser.parse(f);
      // System.out.println(cu);

      cu.removeComment().findAll(FieldDeclaration.class).stream().forEach(fD -> System.out.println("..... "+
        fD.removeComment() + "..... at line: "+ fD.getRange().get().begin.line+" ....."));
      cu.findAll(MethodDeclaration.class).stream().forEach(m -> System.out.println(m.removeComment()));
      // cu.forEach(t -> System.out.println(t));
      // cu.removeComment().forEach(al -> System.out.println(al));

      YamlPrinter printerY = new YamlPrinter(true);
      System.out.println(printerY.output(cu));

      XmlPrinter printerX = new XmlPrinter(true);
      System.out.println(printerX.output(cu));
      System.out.println(hasMultipleBlankLine(args[0]));
    }

    public static List<Range> hasMultipleBlankLine(String file_path) throws FileNotFoundException {
      Scanner scanner = new Scanner(new File(file_path));
      List<Range> ranges = new ArrayList<>();
      int count_empty_line = scanner.nextLine().trim().isEmpty() ? 1 : 0;
      int line = 1;
      boolean has_m_blank_line = false;
      System.out.println("------------- Test for multiple empty line Started -------------");
      while (scanner.hasNextLine()) {
        if(scanner.nextLine().trim().isEmpty()) {
          ++count_empty_line;
        } else {
          if(count_empty_line > 1) {
            int start_pos = line-count_empty_line+1;
            System.out.println("There is "+count_empty_line+" empty lines from line "+start_pos+" to "+line);
            has_m_blank_line = true;
            ranges.add(new Range(new Position(start_pos, 0), new Position(line, 0)));
          }
          count_empty_line = 0;
        }
        ++line;
      }
      if(!has_m_blank_line) {
        System.out.println("There is no multiple empty line back to back");
      }
      System.out.println("............. Test for multiple empty line Ended   .............");
      scanner.close();
      return ranges;
  	}
}
