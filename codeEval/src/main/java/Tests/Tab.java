package Tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.ChildPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimplePropertyDescriptor;
import org.eclipse.jdt.core.dom.Statement;

import Tools.Range;

/**
 * Check tabulation for a certain AST Checks: - new tab after control flow and
 * blocks - tab are the same within the same block
 */
public class Tab {
    private final CompilationUnit cu;

    public Tab(CompilationUnit cu) {
        this.cu = cu;
    }

    public List<Range> tabCheck() {
        cu.accept(
        new ASTVisitor() {
            public boolean visit(MethodDeclaration node) {
                // System.out.println("HERE NODE: "+node.getBody().statements());
                tabBlockChecks(node.getBody().statements());
                return true;
            }
        });
        // BlockVisitor v = new BlockVisitor();
        // cu.accept(v);
        // System.out.println(v.getNodes());
        return null; // TODO: this
    }

    private List<Range> tabBlockChecks(List<Statement> b) {
        if(b.isEmpty()) {
            return new ArrayList<Range>();
        }
        if(isBlockStatement(b.get(0))) {
            System.out.print("HERE " +b.get(0));
            System.out.println(" oui");
        } else {
            System.out.println(" non " + b.get(0).getNodeType());
        }
        // int current_tab = b.get(0);
        for(int i = 1; i < b.size(); ++i) {
            System.out.print("HERE " +b.get(i));
            if(isBlockStatement(b.get(i))) {
                System.out.println(" oui");
            } else {
                System.out.println(" non " + b.get(i).getNodeType());
            }
        }
        return null; // TODO: this
    }

    private boolean isBlockStatement(ASTNode n) {
        return  n.getNodeType() == ASTNode.METHOD_DECLARATION ||
                n.getNodeType() == ASTNode.WHILE_STATEMENT ||
                n.getNodeType() == ASTNode.FOR_STATEMENT ||
                n.getNodeType() == ASTNode.IF_STATEMENT ||
                n.getNodeType() == ASTNode.BLOCK; // TODO: check if there exists more block type
    }
}