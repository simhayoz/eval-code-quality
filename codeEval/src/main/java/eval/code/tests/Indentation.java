package eval.code.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchExpression;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

import eval.code.tools.pos.Position;
import eval.code.tools.pos.SinglePosition;

/**
 * Check Indentation for a CompilationUnit
 * <p>
 * Checks for:
 * <ul>
 * <li>Same indentation for each statements within the same block
 * <li>Greater indentation than parent for each statement inside a block
 * <li>Blocks have the same indentation difference everywhere
 * </ul>
 * 
 * @author Simon Hayoz
 */
public class Indentation extends CUBasedTest {

    private final Map<List<ASTNode>, Integer> block_tab_diff = new HashMap<>();
    private final List<Position> error_range = new ArrayList<>();

    public Indentation(CompilationUnit cu) {
        super(cu);
        NAME = "indentation";
    }

    protected List<Position> test() {
        BlockVisitor v = new BlockVisitor();
        getCU().accept(v);
        Map<Integer, Long> occur_map = block_tab_diff.values().stream()
                .collect(Collectors.groupingBy(k -> k, Collectors.counting()));
        int max_occur = occur_map.entrySet().stream().max((e1, e2) -> e1.getValue() > e2.getValue() ? 1 : -1).get()
                .getKey();
        if (occur_map.size() > 1) {
            block_tab_diff.forEach((l_child, t) -> {
                if (t != max_occur) {
                    SinglePosition start = Position.setPos(getLine(l_child.get(0)), getCol(l_child.get(0)));
                    SinglePosition end = Position.setPos(getLine(l_child.get(l_child.size() - 1)),
                            getCol(l_child.get(l_child.size() - 1)));
                    Position range = Position.setRangeOrSinglePos(start, end);
                    error_range.add(range);
                    printError(range, "a difference of " + t, "a difference of " + max_occur);
                }
            });
        }
        List<Position> p = new ArrayList<>(error_range);
        error_range.clear();
        block_tab_diff.clear();
        return p;
    }

    private void iterateOver(ASTNode parent, ASTNode n) {
        iterateOver(parent, Collections.singletonList(n));
    }

    private void iterateOver(ASTNode parent, List<ASTNode> l) {
        if (l != null && !l.isEmpty() && l.size() == 1) {
            testForCurrNodeExpr(parent, l.get(0));
        } else {
            testNodeBlock(parent, l);
        }
        for (ASTNode n : l) {
            switch (n.getNodeType()) {
                case ASTNode.WHILE_STATEMENT:
                    iterateOverBlockOrStatement(n, ((WhileStatement) n).getBody());
                    break;
                case ASTNode.FOR_STATEMENT:
                    iterateOverBlockOrStatement(n, ((ForStatement) n).getBody());
                    break;
                case ASTNode.ENHANCED_FOR_STATEMENT:
                    iterateOverBlockOrStatement(n, ((EnhancedForStatement) n).getBody());
                    break;
                case ASTNode.IF_STATEMENT:
                    iterateOverBlockOrStatement(n, ((IfStatement) n).getThenStatement());
                    iterateOverBlockOrStatement(n, ((IfStatement) n).getElseStatement());
                    break;
                case ASTNode.BLOCK:
                    iterateOver(n, ((Block) n).statements());
                    break;
                case ASTNode.SWITCH_EXPRESSION:
                    iterateOver(n, ((SwitchExpression) n).statements());
                    break;
                case ASTNode.SWITCH_STATEMENT:
                    iterateOver(n, ((SwitchStatement) n).statements());
                    break;
                case ASTNode.DO_STATEMENT:
                    iterateOverBlockOrStatement(n, ((DoStatement) n).getBody());
                    break;
                case ASTNode.LABELED_STATEMENT:
                    iterateOverBlockOrStatement(n, ((LabeledStatement) n).getBody());
                    break;
                case ASTNode.SYNCHRONIZED_STATEMENT:
                    iterateOverBlockOrStatement(n, ((SynchronizedStatement) n).getBody());
                    break;
                case ASTNode.TRY_STATEMENT:
                    iterateOverBlockOrStatement(n, ((TryStatement) n).getBody());
                    iterateOver(n, ((TryStatement) n).catchClauses());
                    iterateOverBlockOrStatement(n, ((TryStatement) n).getFinally());
                    break;
                case ASTNode.ENUM_DECLARATION:
                    iterateOver(n, ((EnumDeclaration) n).enumConstants());
                    break;
            }
        }
    }

    private void iterateOverBlockOrStatement(ASTNode parent, Statement s) {
        if (s != null) {
            if (s.getNodeType() == ASTNode.BLOCK) {
                iterateOver(parent, ((Block) s).statements());
            } else {
                iterateOver(parent, s);
            }
        }
    }

    private void testNodeBlock(ASTNode parent, List<ASTNode> child_statments) {
        if (!child_statments.isEmpty()) {
            List<SinglePosition> pos = new ArrayList<>();
            for (int i = 0; i < child_statments.size(); ++i) {
                pos.add(Position.setPos(getLine(child_statments.get(i)), getCol(child_statments.get(i))));
            }
            Map<Integer, Long> similarTab = pos.stream().map(s -> s.column)
                    .collect(Collectors.groupingBy(k -> k, Collectors.counting()));
            int max_occurence_tab;
            if (similarTab.size() > 1) {
                max_occurence_tab = similarTab.entrySet().stream()
                        .max((e1, e2) -> e1.getValue() > e2.getValue() ? 1 : -1).get().getKey();
                pos.forEach(p -> {
                    if (p.column != max_occurence_tab) {
                        error_range.add(p);
                        printError(p, p.column, max_occurence_tab);
                    }
                });
            } else {
                max_occurence_tab = pos.get(0).column;
            }
            if (max_occurence_tab <= getCol(parent)) {
                Position p = Position.setRangeOrSinglePos(pos.get(0), pos.get(pos.size() - 1));
                printError(p, max_occurence_tab, "greater than " + getCol(parent));
                error_range.add(p);
            } else {
                block_tab_diff.put(child_statments, max_occurence_tab - getCol(parent));
            }
        }
    }

    private void testForCurrNodeExpr(ASTNode parent, ASTNode child) {
        if (getCol(child) <= getCol(parent)) {
            SinglePosition p = Position.setPos(getLine(child), getCol(child));
            printError(p, getCol(child), "greater than " + getCol(parent));
            error_range.add(p);
        } else {
            block_tab_diff.put(Collections.singletonList(child), getCol(child) - getCol(parent));
        }
    }

    private class BlockVisitor extends ASTVisitor {

        @Override
        public boolean visit(ImportDeclaration n) {
            if (getCol(n) != 0) {
                SinglePosition p = Position.setPos(getLine(n), getCol(n));
                error_range.add(p);
                printError(p, p.column, 0);
            }
            return true;
        }

        @Override
        public boolean visit(PackageDeclaration n) {
            if (getCol(n) != 0) {
                SinglePosition p = Position.setPos(getLine(n), getCol(n));
                error_range.add(p);
                printError(p, p.column, 0);
            }
            return true;
        }

        @Override
        public boolean visit(TypeDeclaration n) {
            testNodeBlock(n, Arrays.asList(n.getFields()));
            testNodeBlock(n, Arrays.asList(n.getMethods()));
            for (MethodDeclaration m : n.getMethods()) {
                iterateOver(m, m.getBody().statements());
            }
            return true;
        }
    }
}