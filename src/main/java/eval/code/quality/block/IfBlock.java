package eval.code.quality.block;

import com.github.javaparser.ast.stmt.IfStmt;
import eval.code.quality.position.SinglePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an if-(else if-)else block.
 */
public class IfBlock extends ParentBlock {

    private final int parentLineEnd;

    public IfBlock(IfStmt ifStmt, String content) {
        super(ifStmt, getRangeOrNull(ifStmt.getThenStmt()),
                getStatements(ifStmt.getThenStmt()), getChildBlocks(ifStmt, content));
        parentLineEnd = Objects.requireNonNull(getIndexNext(content, ")", SinglePosition.from(ifStmt.getCondition().getEnd().get()))).line;
    }

    @Override
    public int getParentLineEnd() {
        return parentLineEnd;
    }

    /**
     * Get all the child blocks of this if statement.
     *
     * @param ifStmt  the if statement
     * @param content the string content from where this if was found
     * @return the list of child block of this if statement
     */
    public static List<ChildBlock> getChildBlocks(IfStmt ifStmt, String content) {
        List<ChildBlock> childBlocks = new ArrayList<>();
        IfStmt temp = ifStmt;
        IfStmt prev = ifStmt;
        while (temp.hasCascadingIfStmt()) {
            temp = temp.getElseStmt().get().asIfStmt();
            IfStmt finalTemp = temp;
            childBlocks.add(new ChildBlock(getIndexNext(content, "else", SinglePosition.from(prev.getThenStmt().getEnd().get())),
                    getRangeOrNull(finalTemp.getThenStmt()), getStatements(finalTemp.getThenStmt())) {
                @Override
                public int getParentLineEnd() {
                    return Objects.requireNonNull(getIndexNext(content, ")", SinglePosition.from(finalTemp.getCondition().getEnd().get()))).line;
                }
            });
            prev = temp;
        }
        final IfStmt tempIf = temp;
        temp.getElseStmt().ifPresent(elseBranch -> childBlocks.add(new ChildBlock(getIndexNext(content, "else", SinglePosition.from(tempIf.getThenStmt().getEnd().get())),
                getRangeOrNull(elseBranch), getStatements(elseBranch))));
        return childBlocks;
    }
}
