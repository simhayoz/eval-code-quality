package eval.code.quality.block;

import com.github.javaparser.ast.body.AnnotationDeclaration;

import java.util.ArrayList;

public class AnnotationBlock extends ParentBlock {

    public AnnotationBlock(AnnotationDeclaration annotationDeclaration) {
        super(annotationDeclaration, annotationDeclaration.getMembers(), new ArrayList<>());
    }

}
