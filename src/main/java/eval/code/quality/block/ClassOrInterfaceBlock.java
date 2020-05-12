package eval.code.quality.block;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.ArrayList;

public class ClassOrInterfaceBlock extends ParentBlock {

    public ClassOrInterfaceBlock(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        super(classOrInterfaceDeclaration, classOrInterfaceDeclaration.getMembers(), new ArrayList<>());
    }

}
