package eval.code.quality;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

public class BlockVisitor extends ASTVisitor {

    List<ASTNode> visited = new ArrayList<>();

    @Override
    public boolean visit(MethodDeclaration n) {
        visit((ASTNode)n);
        return true;
    }

    @Override
    public boolean visit(WhileStatement n) {
        visit((ASTNode)n);
        return true;
    }

    @Override
    public boolean visit(ForStatement n) {
        visit((ASTNode)n);
        return true;
    }

    @Override
    public boolean visit(IfStatement n) {
        visit((ASTNode)n);
        return true;
    }

    @Override
    public boolean visit(Block n) {
        visit((ASTNode)n);
        return true;
    }

    public List<ASTNode> getNodes() {
        return visited;
    }

    private void visit(ASTNode n) {
        visited.add(n);
    }
}