package eval.code.tools;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * Represent a CompilationUnit with more tools
 * <p>
 * Act somehow as a Decorator Pattern (but not exactly)
 * 
 * @author Simon Hayoz
 */
public class ProcessCU {

    private final CompilationUnit cu;

    private ProcessCU(CompilationUnit cu) {
        this.cu = cu;
    }

    /**
     * Get the CompilationUnit for the current instance
     * 
     * @return the CompilationUnit
     */
    public CompilationUnit getCU() {
        return cu;
    }

    /**
     * Create a new ProcessCU with a constructed CompilationUnit from the specified
     * file
     * 
     * @param file_path path to the file
     * @return the newly created ProcessCU
     * @throws FileNotFoundException
     */
    public static ProcessCU fromPath(String file_path) throws FileNotFoundException {
        return fromFile(new File(file_path));
    }

    /**
     * Create a new ProcessCU with a constructed CompilationUnit from the specified
     * file content
     * 
     * @param f file
     * @return the newly created ProcessCU
     * @throws FileNotFoundException
     */
    public static ProcessCU fromFile(File f) throws FileNotFoundException {
        return new ProcessCU(getASTFromContent(SFile.stringFromFile(f).toCharArray()));
    }

    /**
     * Create a new ProcessCU with a constructed CompilationUnit from the specified
     * string
     * 
     * @param s String of the content
     * @return the newly created ProcessCU
     */
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