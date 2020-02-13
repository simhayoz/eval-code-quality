package TestEclipse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.EmptyStatement;

public class TestVisitor extends ASTVisitor {
  List<MethodDeclaration> method_declarations = new ArrayList<>();
  @Override
  public boolean visit(MethodDeclaration n) {
    System.out.println(n.getName() + " at " + n.getStartPosition());
    method_declarations.add(n);
    return true;
  }

  @Override
  public boolean visit(EmptyStatement n) {
    System.out.println(n);
    System.out.println("Oui");
    return true;
  }

  public List<MethodDeclaration> getMethodDeclarations() {
    return new ArrayList(method_declarations);
  }
}
