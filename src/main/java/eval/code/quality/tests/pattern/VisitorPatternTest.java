package eval.code.quality.tests.pattern;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.tests.DesignPatternTest;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class VisitorPatternTest extends DesignPatternTest {

    private final String parentName;
    private final List<String> childrenName;
    private final String visitorName;

    public VisitorPatternTest(ContentProvider contentProvider, String parentName, List<String> childrenName, String visitorName) {
        super(contentProvider);
        this.parentName = parentName;
        this.childrenName = childrenName;
        this.visitorName = visitorName;
    }

    @Override
    public boolean enforce(ContentProvider contentProvider) {
        ClassOrInterfaceDeclaration parent = contentProvider.findClassBy(parentName).get();
        List<ClassOrInterfaceDeclaration> children = childrenName.stream().map(cName -> contentProvider.findClassBy(cName).get()).collect(Collectors.toList());
        ClassOrInterfaceDeclaration visitor = contentProvider.findClassBy(visitorName).get();
//        Supplier<Boolean> hasMethodVisit = () -> visitor.getMethods().stream().filter(m -> m.getParameters().stream().filter(p -> p.getType().equals(children.get(0).getTypeParameters()))).map(m -> m.getParameters()).collect(toString());
//        System.out.println(visitor.getMethods().stream().map(m -> m.getParameters().toString()).collect(Collectors.joining(System.lineSeparator())));
        /*
            to check:
                 - visitor has at least one method "visit" per child as argument
                 - parent has method accept (to be implemented by children) or child all have it
         */
        return false;
    }

    @Override
    public void describeMismatch() {
        // TODO this
    }

    @Override
    protected String getName() {
        return "visitor pattern for parent " + addChevrons(parentName) + ", children " + addChevrons(childrenName.toString()) + " and visitor " + addChevrons(visitorName);
    }
}
