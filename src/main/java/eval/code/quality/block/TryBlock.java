package eval.code.quality.block;

import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.TryStmt;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a try-catch block.
 */
public class TryBlock extends ParentBlock {

    public TryBlock(TryStmt tryStmt, String content) {
        super(tryStmt, Range.from(tryStmt.getTryBlock().getRange().get()), tryStmt.getTryBlock().getStatements(), getChildBlocks(tryStmt, content));
    }

    public static List<ChildBlock> getChildBlocks(TryStmt tryStmt, String content) {
        List<ChildBlock> childBlocks = new ArrayList<>();
        for (CatchClause catchClause : tryStmt.getCatchClauses()) {
            childBlocks.add(new ChildBlock(SinglePosition.from(catchClause.getBegin().get()), Range.from(catchClause.getBody().getRange().get()), catchClause.getBody().getStatements()) {
                @Override
                public int getParentLineEnd() {
                    return Objects.requireNonNull(getIndexNext(content, ")", SinglePosition.from(catchClause.getParameter().getEnd().get()))).line;
                }
            });
        }
        tryStmt.getFinallyBlock().ifPresent(finalElement ->
                childBlocks.add(new ChildBlock(getIndexNext(content, "finally", SinglePosition.from(tryStmt.getCatchClauses().get(tryStmt.getCatchClauses().size()-1).getEnd().get())),
                        Range.from(finalElement.getRange().get()),
                        finalElement.getStatements())));
        return childBlocks;
    }
}
