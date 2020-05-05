package eval.code.quality.provider;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a content provider.
 */
public abstract class ContentProvider implements Iterable<ContentProvider> {

    /**
     * Get the string of the {@code ContentProvider}.
     *
     * @return the string of the {@code ContentProvider}
     */
    public abstract String getString();

    /**
     * Get the {@code CompilationUnit} of the {@code ContentProvider}.
     *
     * @return the {@code CompilationUnit} of the {@code ContentProvider}
     */
    public abstract CompilationUnit getCompilationUnit();

    /**
     * Add all child {@code ContentProvider} to the list of {@code ContentProvider} for easier iteration.
     *
     * @param contentProviders the current list of {@code ContentProvider}
     */
    public abstract void addAll(List<ContentProvider> contentProviders);

    /**
     * Get the name of the {@code ContentProvider}.
     *
     * @return the name of the {@code ContentProvider}
     */
    public abstract String getName();

    /**
     * Find a class by name in the content provider.
     *
     * @param name the name of the class
     * @return the class named {@code name}
     */
    public Optional<ClassOrInterfaceDeclaration> findClassBy(String name) {
        List<ContentProvider> providers = new ArrayList<>();
        addAll(providers);
        for (ContentProvider contentProvider : providers) {
            Optional<ClassOrInterfaceDeclaration> optClass = contentProvider.getCompilationUnit().findAll(ClassOrInterfaceDeclaration.class).stream().filter(c ->
                    c.getNameAsString().equals(name)).findFirst();
            if (optClass.isPresent()) {
                return optClass;
            }
        }
        return Optional.empty();
    }

    public Optional<MethodDeclaration> findMethodBy(String name, String className) {
        return findClassBy(className).map(c -> c.getMethods().stream().filter(m -> m.getNameAsString().equals(name)).findFirst()).orElse(Optional.empty());
    }

//    public Optional<List<ClassOrInterfaceDeclaration>> findAllClassBy(String name) {
//        List<ClassOrInterfaceDeclaration> result = new ArrayList<>();
//        List<ContentProvider> providers = new ArrayList<>();
//        addAll(providers);
//        for (ContentProvider contentProvider : providers) {
//            result.addAll(
//                    contentProvider.getCompilationUnit().findAll(ClassOrInterfaceDeclaration.class).stream().filter(c ->
//                            c.getNameAsString().equals(name)).collect(Collectors.toList()));
//        }
//        if (result.size() > 0) {
//            return Optional.of(result);
//        }
//        return Optional.empty();
//    }

    @Override
    public Iterator<ContentProvider> iterator() {
        List<ContentProvider> list = new ArrayList<>();
        addAll(list);
        return list.iterator();
    }
}
