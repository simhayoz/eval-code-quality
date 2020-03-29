package eval.code.tests;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * Super class of all tests base on a CompilationUnit
 * 
 * @author Simon Hayoz
 */
public abstract class CUBasedTest extends Test {
    private final CompilationUnit cu;
    private final String[] content_by_line;

    public CUBasedTest(CompilationUnit cu, String content) {
        this.cu = cu;
        this.content_by_line = content.split(System.lineSeparator());
    }

    protected int getLine(ASTNode n) {
        if (n instanceof BodyDeclaration) {
            if (n instanceof MethodDeclaration)
                return getLine(getPosNoJavaDoc((BodyDeclaration) n));
        }
        return getLine(n.getStartPosition());
    }

    protected int getLine(int position) {
        return cu.getLineNumber(position);
    }

    protected int getCol(ASTNode n) {
        if (n instanceof BodyDeclaration) {
            return getCol(getPosNoJavaDoc((BodyDeclaration) n));
        }
        return getCol(n.getStartPosition());
    }

    protected int getCol(int position) {
        return cu.getColumnNumber(position);
    }

    private int getPosNoJavaDoc(BodyDeclaration b) {
        if(b.getJavadoc() != null) {
            int javaDocLength = b.getJavadoc().getLength();
            int declAtLine = getLine(javaDocLength + b.getStartPosition());
            int emptyLineSpace = 0;
            String declLine = content_by_line[declAtLine];
            while(declLine.trim().isEmpty()) {
                emptyLineSpace += declLine.length() + 1;
                declLine = content_by_line[++declAtLine];
            }
            int lineIndentLength = declLine.indexOf(declLine.trim());
            return b.getStartPosition() + javaDocLength + lineIndentLength + emptyLineSpace + 1;
        }
        return b.getStartPosition();
    }

    protected CompilationUnit getCU() {
        return cu;
    }
}