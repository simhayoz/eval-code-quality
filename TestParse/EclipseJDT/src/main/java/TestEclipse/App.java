/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package TestEclipse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.ChildPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimplePropertyDescriptor;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
      char[] content;
      try {
        content = fileToCharArray(args[0]);
        final CompilationUnit cu = getASTFromContent(content);
        // System.out.println(cu);
        TestVisitor visitor = new TestVisitor();
        cu.accept(visitor);
        List<MethodDeclaration> l = visitor.getMethodDeclarations();
        System.out.println(l);
        System.out.println(hasMultipleBlankLine(args[0]));
      }
      catch(FileNotFoundException e) {

      }
    }

    public static CompilationUnit getASTFromContent(char[] content) {
      ASTParser parser = ASTParser.newParser(AST.JLS13);
      parser.setSource(content);
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      return (CompilationUnit) parser.createAST(null);
    }

    public static char[] fileToCharArray(String file_path) throws FileNotFoundException {
      String s = "";
      Scanner scanner = new Scanner(new File(file_path));
      s = scanner.nextLine();
      while (scanner.hasNextLine()) {
        s += "\n" + scanner.nextLine();
      }
      scanner.close();
      return s.toCharArray();
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
