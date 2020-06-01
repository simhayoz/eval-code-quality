package eval.code.quality.block;

import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.TryStmt;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;

import java.util.ArrayList;
import java.util.List;

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
            childBlocks.add(new ChildBlock(SinglePosition.from(catchClause.getBegin().get()), Range.from(catchClause.getBody().getRange().get()), catchClause.getBody().getStatements()));
        }
        tryStmt.getFinallyBlock().ifPresent(finalElement ->
                childBlocks.add(new ChildBlock(getIndexNext(content, "finally", tryStmt.getBegin().get().line),
                        Range.from(finalElement.getRange().get()),
                        finalElement.getStatements())));
        return childBlocks;
    }
}
