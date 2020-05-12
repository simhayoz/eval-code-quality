package eval.code.quality.block;

import com.github.javaparser.ast.stmt.IfStmt;

import java.util.ArrayList;
import java.util.List;

public class IfBlock extends ParentBlock {

    public IfBlock(IfStmt ifStmt, String content) {
        super(ifStmt, getRangeOrNull(ifStmt.getThenStmt()),
                getStatements(ifStmt.getThenStmt()), getChildBlocks(ifStmt, content));
    }

    public static List<ChildBlock> getChildBlocks(IfStmt ifStmt, String content) {
        List<ChildBlock> childBlocks = new ArrayList<>();
        IfStmt temp = ifStmt;
        IfStmt prev = ifStmt;
        while(temp.hasCascadingIfStmt()) {
            temp = temp.getElseStmt().get().asIfStmt();
            childBlocks.add(new ChildBlock(getIndexNext(content, "else", getStartingLine(prev)),
                    getRangeOrNull(temp.getThenStmt()), getStatements(temp.getThenStmt())));
            prev = temp;
        }
        final int tempLine = getStartingLine(temp);
        temp.getElseStmt().ifPresent(elseBranch -> childBlocks.add(new ChildBlock(getIndexNext(content, "else", tempLine),
                getRangeOrNull(elseBranch), getStatements(elseBranch))));
        return childBlocks;
    }

    private static int getStartingLine(IfStmt ifStmt) {
        return ifStmt.getThenStmt().getEnd().get().line;
    }
}
