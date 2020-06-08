package eval.code.quality.block;

import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import eval.code.quality.position.SinglePosition;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a class or interface block.
 */
public class ClassOrInterfaceBlock extends ParentBlock {

    private final int parentLineEnd;

    public ClassOrInterfaceBlock(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        super(classOrInterfaceDeclaration, getRangeFromBodyDeclaration(classOrInterfaceDeclaration), classOrInterfaceDeclaration.getMembers(), new ArrayList<>());
        parentLineEnd = Objects.requireNonNull(getPositionLastNonNull(classOrInterfaceDeclaration)).line;
    }

    private static SinglePosition getPositionLastNonNull(Node node) {
        SinglePosition lastNonNull = null;
        for(JavaToken token : node.getTokenRange().get()) {
            if(token.asString().equals("{")) {
                return lastNonNull;
            } else if(!token.asString().trim().isEmpty()){
                lastNonNull = SinglePosition.from(token.getRange().get().begin);
            }
        }
        return null;
    }

    @Override
    public int getParentLineEnd() {
        return parentLineEnd;
    }

}
