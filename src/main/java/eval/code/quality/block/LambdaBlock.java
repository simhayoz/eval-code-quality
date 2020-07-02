package eval.code.quality.block;

import com.github.javaparser.Position;
import com.github.javaparser.ast.expr.LambdaExpr;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a lambda block.
 */
public class LambdaBlock extends ParentBlock {

    private Position parentStart;
    private int parentLineEnd;

    public LambdaBlock(LambdaExpr lambdaExpr, String content) {
        super(lambdaExpr, getRangeOrNull(lambdaExpr.getBody().asBlockStmt()), getStatements(lambdaExpr.getBody().asBlockStmt()), new ArrayList<>());
        lambdaExpr.getBegin().ifPresent(pos -> {
            int columnStart = getIndexFirstElementLine(content, pos.line);
            if (pos.column == columnStart) {
                this.parentStart = parent.getBegin().get();
            } else {
                this.parentStart = Position.pos(pos.line, columnStart);
            }
        });
        if(lambdaExpr.isEnclosingParameters()) {
            parentLineEnd = Objects.requireNonNull(getPositionNextParenthesis(lambdaExpr)).line;
        } else {
            parentLineEnd = lambdaExpr.getParameters().get(0).getEnd().get().line;
        }
    }

    @Override
    public Position getParentStart() {
        return parentStart;
    }

    @Override
    public int getParentLineEnd() {
        return parentLineEnd;
    }

    private static int getIndexFirstElementLine(String content, int line) {
        String lineContent = content.split(System.lineSeparator())[line - 1];
        return lineContent.indexOf(lineContent.trim()) + 1;
    }
}
