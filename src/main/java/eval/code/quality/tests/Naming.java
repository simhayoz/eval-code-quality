package eval.code.quality.tests;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import eval.code.quality.position.Position;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.Context;
import eval.code.quality.utils.MultiplePossibility;
import eval.code.quality.utils.NameProperty;

import java.util.*;
import java.util.stream.Collectors;

public class Naming extends CompilationUnitTest {
    private final Map<Modifiers, Map<Position, NameProperty>> classDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<Position, NameProperty>> enumDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<Position, NameProperty>> annotationDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<Position, NameProperty>> methodDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<Position, NameProperty>> fieldDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<Position, NameProperty>> variableDeclarations = new HashMap<>();


    public Naming(Context context) {
        super(context);
    }

    @Override
    protected void testFor(ContentProvider contentProvider) {
        CompilationUnit compilationUnit = contentProvider.getCompilationUnit();
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(type -> addToMap(classDeclarations, type.getModifiers(), context.getPos(type), type.getNameAsString()));
        compilationUnit.findAll(EnumDeclaration.class).forEach(type -> addToMap(enumDeclarations, type.getModifiers(), context.getPos(type), type.getNameAsString()));
        compilationUnit.findAll(AnnotationDeclaration.class).forEach(type -> addToMap(annotationDeclarations, type.getModifiers(), context.getPos(type), type.getNameAsString()));
        compilationUnit.findAll(MethodDeclaration.class).forEach(method -> addToMap(methodDeclarations, method.getModifiers(), context.getPos(method), method.getNameAsString()));
        compilationUnit.findAll(FieldDeclaration.class).forEach(field -> field.getVariables().forEach(variable -> {
            addToMap(fieldDeclarations, field.getModifiers(), context.getPos(field), variable.getNameAsString());
        }));
        compilationUnit.findAll(VariableDeclarationExpr.class).forEach(variableDeclaration -> variableDeclaration.getVariables().forEach(variable -> addToMap(variableDeclarations, variableDeclaration.getModifiers(), context.getPos(variableDeclaration), variable.getNameAsString())));
    }

    @Override
    protected void afterTests() {
        classDeclarations.forEach(this::testName);
        enumDeclarations.forEach(this::testName);
        annotationDeclarations.forEach(this::testName);
        methodDeclarations.forEach(this::testName);
        fieldDeclarations.forEach(this::testName);
        variableDeclarations.forEach(this::testName);
    }

    private void testName(Modifiers modifiers, Map<Position, NameProperty> map) {
        if(map.size() > 1) {
            NameProperty pName = null;
            Set<NameProperty> property_set = new HashSet<>(map.values());
            for (NameProperty n : property_set) {
                if (pName != null && !n.isLogicEquals(pName)) {
                    Map<Position, String> mapPositions = map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().toString()));
                    if (modifiers != null) {
                        addError(MultiplePossibility.at(mapPositions, "Expected same naming convention for the same modifiers:" + modifiers));
                    } else {
                        addError(MultiplePossibility.at(mapPositions, "Expected same naming convention"));
                    }
                } else {
                    pName = n;
                }
            }
        }
    }

    private <T> void addToMap(Map<Modifiers, Map<T, NameProperty>> map, NodeList<Modifier> modifierList, T node, String name) {
        Modifiers modifiers = new Modifiers();
        modifierList.forEach(e -> modifiers.modifiers.add(e.getKeyword()));
        NameProperty nameProperty = NameProperty.getFor(name);
        if(map.containsKey(modifiers)) {
            Map<T, NameProperty> value = map.get(modifiers);
            value.put(node, nameProperty);
            map.replace(modifiers, value);
        } else {
            Map<T, NameProperty> value = new HashMap<>();
            value.put(node, nameProperty);
            map.put(modifiers, value);
        }
    }

    @Override
    protected String getName() {
        return "naming";
    }

    private static class Modifiers {
        public final List<Modifier.Keyword> modifiers = new ArrayList<>();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Modifiers modifiers1 = (Modifiers) o;
            return this.modifiers.equals(modifiers1.modifiers);
        }

        @Override
        public int hashCode() {
            return modifiers.hashCode();
        }

        @Override
        public String toString() {
            return modifiers.toString();
        }
    }
}
