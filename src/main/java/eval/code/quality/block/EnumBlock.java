package eval.code.quality.block;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.EnumDeclaration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnumBlock extends ParentBlock {

    public EnumBlock(EnumDeclaration enumDeclaration) {
        super(enumDeclaration, getStatements(enumDeclaration), new ArrayList<>());
    }

    private static List<? extends Node> getStatements(EnumDeclaration enumDeclaration) {
        List<? extends Node> statements = enumDeclaration.getEntries();
        if(!statements.isEmpty()) {
            if(statements.get(0).getBegin().get().line == statements.get(statements.size()-1).getBegin().get().line) {
                return Collections.singletonList(statements.get(0));
            } else {
                return statements;
            }
        }
        return new ArrayList<>();
    }

}
