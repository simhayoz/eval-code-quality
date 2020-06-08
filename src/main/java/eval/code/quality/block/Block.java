package eval.code.quality.block;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BlockStmt;
import eval.code.quality.position.Range;

import java.util.ArrayList;

/**
 * Represents any type of statement with a parent and a block in a java code.
 */
public class Block extends ParentBlock {

    public Block(Node parentNode, BlockStmt blockStmt) {
        super(parentNode instanceof BlockStmt ? blockStmt : parentNode, Range.from(blockStmt.getRange().get()), blockStmt.getStatements(), new ArrayList<>());
    }

}
