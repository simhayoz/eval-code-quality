package eval.code.quality.utils.complexity;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;

public interface ComplexityEstimator<T extends Node> {

    /**
     * Estimate complexity for the element.
     * @param element the element to estimate
     * @param methodDeclaration the method declaration surrounding the element
     * @return the complexity
     */
    Complexity getComplexityFor(T element, MethodDeclaration methodDeclaration);
}
