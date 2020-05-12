package eval.code.quality.block;

import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.stmt.SwitchEntry;

import java.util.ArrayList;

public class SwitchEntryBlock extends ParentBlock {

    public SwitchEntryBlock(SwitchEntry switchEntry) {
        super(switchEntry, switchEntry.getStatements(), new ArrayList<>());
    }

}
