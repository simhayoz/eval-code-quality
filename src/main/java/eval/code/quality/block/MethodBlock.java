package eval.code.quality.block;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.util.regex.Pattern;

public class MethodBlock extends Block {

    private int parentLineEnd;

    public MethodBlock(MethodDeclaration methodDeclaration, BlockStmt blockStmt, String content) {
        super(methodDeclaration, blockStmt);
        if(methodDeclaration.asCallableDeclaration().getThrownExceptions().isNonEmpty()) {
            parentLineEnd = methodDeclaration.asCallableDeclaration().getThrownExceptions().get(0).getEnd().get().line;
        } else if(methodDeclaration.getParameters().isNonEmpty()){
            parentLineEnd = getIndexNext(content, ")", methodDeclaration.getParameters().get(methodDeclaration.getParameters().size()-1).getEnd().get().line).line;
        } else {
            parentLineEnd = getIndexNext(content, Pattern.compile("[(][\\n\\r\\s]*[)]"), methodDeclaration.getBegin().get().line).line;
            System.out.println(parentLineEnd);
        }
    }

    @Override
    public int getParentLineEnd() {
        return parentLineEnd;
    }
}
