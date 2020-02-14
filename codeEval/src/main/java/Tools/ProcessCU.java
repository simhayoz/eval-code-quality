package Tools;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ProcessCU {

    private final CompilationUnit cu;

    private ProcessCU(CompilationUnit cu) {
        this.cu = cu;
    }

    public CompilationUnit getCU() {
        return cu;
    }

    public static ProcessCU fromFile(File f) throws FileNotFoundException {
        return new ProcessCU(getASTFromContent(SFile.stringFromFile(f).toCharArray()));
    }

    public static ProcessCU fromString(String s) {
        return new ProcessCU(getASTFromContent(s.toCharArray()));
    }

    private static CompilationUnit getASTFromContent(char[] content) {
        ASTParser parser = ASTParser.newParser(AST.JLS13);
        parser.setSource(content);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        return (CompilationUnit) parser.createAST(null);
    }
}