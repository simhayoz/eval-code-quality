package eval.code.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import eval.code.tools.pos.Position;
import eval.code.tools.pos.PositionList;

public class BracketMatching extends CUBasedTest {

    private final List<ASTNode> one_line_statements = new ArrayList<>();

    public BracketMatching(CompilationUnit cu) {
        super(cu);
        NAME = "bracket matching";
    }

    @Override
    protected void test() {
        getCU().accept(new ASTVisitor() {
            @Override
            public boolean visit(IfStatement node) {
                addOneLiner(node.getThenStatement());
                addOneLiner(node.getElseStatement());
                return true;
            }

            @Override
            public boolean visit(ForStatement node) {
                addOneLiner(node.getBody());
                return true;
            }

            @Override
            public boolean visit(EnhancedForStatement node) {
                addOneLiner(node.getBody());
                return true;
            }

            @Override
            public boolean visit(DoStatement node) {
                addOneLiner(node.getBody());
                return true;
            }

            @Override
            public boolean visit(WhileStatement node) {
                addOneLiner(node.getBody());
                return true;
            }
        });
        Map<OneLiner, List<ASTNode>> m = one_line_statements.stream().collect(Collectors.groupingBy(e -> {
            return e.getNodeType() == ASTNode.BLOCK ? OneLiner.BRACKET : OneLiner.NO_BRACKET;
        }));
        if (m.size() > 1) {
            List<Position> p = one_line_statements.stream().map(e -> {
                return Position.setPos(getLine(e), getCol(e));
            }).collect(Collectors.toList());
            addError(new PositionList(p), "all one liner statements with bracket or all without", "both");
        }
        one_line_statements.clear();
    }

    private void addOneLiner(ASTNode n) {
        if (n != null && (n.getNodeType() != ASTNode.BLOCK || ((Block) n).statements().size() == 1)) {
            one_line_statements.add(n);
        }
    }

    private enum OneLiner {
        BRACKET, NO_BRACKET
    }
}