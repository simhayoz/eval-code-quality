package eval.code.quality.block;

import com.github.javaparser.ast.body.AnnotationDeclaration;
import eval.code.quality.position.Range;

import java.util.ArrayList;

/**
 * Represents a annotation block: @interface X { ... }.
 */
public class AnnotationBlock extends ParentBlock {

    public AnnotationBlock(AnnotationDeclaration annotationDeclaration) {
        super(annotationDeclaration, getRangeFromBodyDeclaration(annotationDeclaration), annotationDeclaration.getMembers(), new ArrayList<>());
    }

}
