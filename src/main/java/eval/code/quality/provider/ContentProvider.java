package eval.code.quality.provider;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
            Optional<ClassOrInterfaceDeclaration> optClass = contentProvider.getCompilationUnit().findAll(ClassOrInterfaceDeclaration.class)
                    .stream().filter(n -> {
                        ClassOrInterfaceDeclaration tempClass = n;
                        StringBuilder finalName = new StringBuilder(tempClass.getNameAsString());
                        while(tempClass.isNestedType()) {
                            tempClass = (ClassOrInterfaceDeclaration) tempClass.getParentNode().get();
                            finalName.insert(0, tempClass.getNameAsString() + "$");
                        }
                        return finalName.toString().equals(name);
                    }).findFirst();
            if (optClass.isPresent()) {
                return optClass;
            }
        }
        return Optional.empty();
    }

    @Override
    @Nonnull
    public Iterator<ContentProvider> iterator() {
        List<ContentProvider> list = new ArrayList<>();
        addAll(list);
        return list.iterator();
    }
}
