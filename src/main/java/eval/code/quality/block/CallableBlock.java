package eval.code.quality.block;

import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.util.regex.Pattern;

public class CallableBlock extends Block {

    private int parentLineEnd;

    public CallableBlock(CallableDeclaration<?> callableDeclaration, BlockStmt blockStmt, String content) {
        super(callableDeclaration, blockStmt);
        if(callableDeclaration.asCallableDeclaration().getThrownExceptions().isNonEmpty()) {
            parentLineEnd = callableDeclaration.asCallableDeclaration().getThrownExceptions().get(0).getEnd().get().line;
        } else if(callableDeclaration.getParameters().isNonEmpty()){
            parentLineEnd = getIndexNext(content, ")", callableDeclaration.getParameters().get(callableDeclaration.getParameters().size()-1).getEnd().get().line).line;
        } else {
            parentLineEnd = getIndexNext(content, Pattern.compile("[(][\\n\\r\\s]*[)]"), callableDeclaration.getBegin().get().line).line;
            System.out.println(parentLineEnd);
        }
    }

    @Override
    public int getParentLineEnd() {
        return parentLineEnd;
    }
}
