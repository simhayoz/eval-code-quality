package eval.code.quality.block;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BlockStmt;
import eval.code.quality.position.Range;

import java.util.ArrayList;

public class Block extends ParentBlock {

    public Block(Node parentNode, BlockStmt blockStmt) {
        super(parentNode instanceof BlockStmt ? blockStmt : parentNode, Range.from(blockStmt.getRange().get()), blockStmt.getStatements(), new ArrayList<>());
    }

}
