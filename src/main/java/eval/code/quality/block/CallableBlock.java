package eval.code.quality.block;

import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.util.Objects;

/**
 * Represents a constructor or a method declaration that is callable.
 */
public class CallableBlock extends Block {

    private final int parentLineEnd;

    public CallableBlock(CallableDeclaration<?> callableDeclaration, BlockStmt blockStmt, String content) {
        super(callableDeclaration, blockStmt);
        if (callableDeclaration.asCallableDeclaration().getThrownExceptions().isNonEmpty()) {
            parentLineEnd = callableDeclaration.asCallableDeclaration().getThrownExceptions().get(0).getEnd().get().line;
        } else {
            parentLineEnd = Objects.requireNonNull(getPositionNextParenthesis(callableDeclaration)).line;
        }
    }

    @Override
    public int getParentLineEnd() {
        return parentLineEnd;
    }
}
