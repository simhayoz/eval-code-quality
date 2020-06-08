package eval.code.quality.block;

import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import eval.code.quality.position.SinglePosition;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a constructor or a method declaration that is callable.
 */
public class CallableBlock extends Block {

    private final int parentLineEnd;

    public CallableBlock(CallableDeclaration<?> callableDeclaration, BlockStmt blockStmt, String content) {
        super(callableDeclaration, blockStmt);
        if (callableDeclaration.asCallableDeclaration().getThrownExceptions().isNonEmpty()) {
            parentLineEnd = callableDeclaration.asCallableDeclaration().getThrownExceptions().get(0).getEnd().get().line;
        } else if (callableDeclaration.getParameters().isNonEmpty()) {
            parentLineEnd = Objects.requireNonNull(getIndexNext(content, ")", SinglePosition.from(callableDeclaration.getParameters().get(callableDeclaration.getParameters().size() - 1).getEnd().get()))).line;
        } else {
            parentLineEnd = Objects.requireNonNull(getIndexNext(content, Pattern.compile("[(][\\n\\r\\s]*[)]"), callableDeclaration.getBegin().get().line)).line;
        }
    }

    @Override
    public int getParentLineEnd() {
        return parentLineEnd;
    }
}
