package eval.code.tests;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * Super class of all tests base on a CompilationUnit
 * 
 * @author Simon Hayoz
 */
public abstract class CUBasedTest extends Test {
    private final CompilationUnit cu;

    public CUBasedTest(CompilationUnit cu) {
        this.cu = cu;
    }

    protected int getLine(ASTNode n) {
        return getLine(n.getStartPosition());
    }

    protected int getLine(int position) {
        return cu.getLineNumber(position);
    }

    protected int getCol(ASTNode n) {
        return getCol(n.getStartPosition());
    }

    protected int getCol(int position) {
        return cu.getColumnNumber(position);
    }

    protected CompilationUnit getCU() {
        return cu;
    }
}