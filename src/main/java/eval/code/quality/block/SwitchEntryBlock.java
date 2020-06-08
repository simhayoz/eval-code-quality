package eval.code.quality.block;

import com.github.javaparser.ast.stmt.SwitchEntry;

import java.util.ArrayList;

/**
 * Represents a switch entry block.
 */
public class SwitchEntryBlock extends ParentBlock {

    public SwitchEntryBlock(SwitchEntry switchEntry) {
        super(switchEntry, switchEntry.getStatements(), new ArrayList<>());
    }

}
