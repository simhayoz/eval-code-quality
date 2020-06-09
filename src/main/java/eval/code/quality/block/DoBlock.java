package eval.code.quality.block;

import com.github.javaparser.ast.stmt.DoStmt;
import eval.code.quality.position.SinglePosition;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents a do-while block.
 */
public class DoBlock extends ParentBlock {

    public DoBlock(DoStmt doStmt, String content) {
        super(doStmt,
                getRangeOrNull(doStmt.getBody()),
                getStatements(doStmt.getBody()),
                Collections.singletonList(new ChildBlock(getIndexNext(content, "while", SinglePosition.from(doStmt.getBody().getEnd().get())), null, new ArrayList<>())));
    }
}
