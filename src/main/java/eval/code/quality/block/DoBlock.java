package eval.code.quality.block;

import com.github.javaparser.ast.stmt.DoStmt;

import java.util.ArrayList;
import java.util.Collections;

public class DoBlock extends ParentBlock {

    public DoBlock(DoStmt doStmt, String content) {
        super(doStmt,
                getRangeOrNull(doStmt.getBody()),
                getStatements(doStmt.getBody()),
                Collections.singletonList(new ChildBlock(getIndexNext(content, "while", doStmt.getBegin().get().line), null, new ArrayList<>())));
    }
}
