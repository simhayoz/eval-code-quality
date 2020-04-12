package eval.code.quality.tests;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import eval.code.quality.position.MultiplePosition;
import eval.code.quality.position.Position;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Check that name for class, method, enum, variable, etc use the same naming convention for the same set of modifiers.
 */
public class Naming extends CompilationUnitTest {
    private final Map<Modifiers, Map<NameProperty, List<Position>>> classDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<NameProperty, List<Position>>> enumDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<NameProperty, List<Position>>> enumValues = new HashMap<>();
    private final Map<Modifiers, Map<NameProperty, List<Position>>> annotationDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<NameProperty, List<Position>>> methodDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<NameProperty, List<Position>>> fieldDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<NameProperty, List<Position>>> variableDeclarations = new HashMap<>();


    public Naming(Context context) {
        super(context);
    }

    @Override
    protected void testFor(ContentProvider contentProvider) {
        CompilationUnit compilationUnit = contentProvider.getCompilationUnit();
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(type -> addToMap(classDeclarations, type.getModifiers(), context.getPos(type), type.getNameAsString()));
        compilationUnit.findAll(EnumDeclaration.class).forEach(type -> addToMap(enumDeclarations, type.getModifiers(), context.getPos(type), type.getNameAsString()));
        compilationUnit.findAll(EnumConstantDeclaration.class).forEach(enumConstantDeclaration -> addToMap(enumValues, new NodeList<>(), context.getPos(enumConstantDeclaration), enumConstantDeclaration.getNameAsString()));
        compilationUnit.findAll(AnnotationDeclaration.class).forEach(type -> addToMap(annotationDeclarations, type.getModifiers(), context.getPos(type), type.getNameAsString()));
        compilationUnit.findAll(MethodDeclaration.class).forEach(method -> addToMap(methodDeclarations, method.getModifiers(), context.getPos(method), method.getNameAsString()));
        compilationUnit.findAll(FieldDeclaration.class).forEach(field -> field.getVariables().forEach(variable -> {
            addToMap(fieldDeclarations, field.getModifiers(), context.getPos(field), variable.getNameAsString());
        }));
        compilationUnit.findAll(VariableDeclarationExpr.class).forEach(variableDeclaration -> variableDeclaration.getVariables().forEach(variable ->
                addToMap(variableDeclarations, variableDeclaration.getModifiers(), context.getPos(variableDeclaration), variable.getNameAsString())));
    }

    @Override
    protected void afterTests() {
        classDeclarations.forEach(this::testName);
        enumDeclarations.forEach(this::testName);
        enumValues.forEach(this::testName);
        annotationDeclarations.forEach(this::testName);
        methodDeclarations.forEach(this::testName);
        fieldDeclarations.forEach(this::testName);
        variableDeclarations.forEach(this::testName);
    }

    private void testName(Modifiers modifiers, Map<NameProperty, List<Position>> map) {
        if(map.size() > 1) {
            List<Map.Entry<NameProperty, List<Position>>> orderedList = new ArrayList<>(map.entrySet());
            orderedList.sort((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()));
            List<Map.Entry<NameProperty, List<Position>>> sameSize = new ArrayList<>();
            List<Map.Entry<NameProperty, List<Position>>> smallerSize = new ArrayList<>();
            int maxSize = orderedList.get(0).getValue().size();
            orderedList.forEach(e -> (e.getValue().size() == maxSize ? sameSize : smallerSize).add(e)); // TODO reverse order for sameSize and smallerSize
            Node<NameProperty> root = NamePropertyTree.getCurrentNodeForTree(orderedList.get(0).getKey());
            if(sameSize.size() > 1) {
                Map<Position, NameProperty> accumulator = new HashMap<>();
                checkIsInSameTreePath(sameSize.iterator(), root, accumulator);
                if(!accumulator.isEmpty()) {
                    accumulator.put(new MultiplePosition(orderedList.get(0).getValue()), orderedList.get(0).getKey());
                    addError(MultiplePossibility.at(
                            accumulator.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString())),
                            modifiers != null ? "Expected same naming convention for the same modifiers:" + modifiers : "Expected same naming convention"));
                }
            }
            if(!smallerSize.isEmpty()) {
                Map<Position, NameProperty> accumulator = new HashMap<>();
                NameProperty expected = checkIsInSameTreePath(smallerSize.iterator(), root, accumulator);
                accumulator.forEach((k, v) -> addError(ReportPosition.at(k,
                        (modifiers != null ? "for the modifiers: " + modifiers + ": " : "") + expected, v.toString())));
            }
        }
    }

    private NameProperty checkIsInSameTreePath(Iterator<Map.Entry<NameProperty, List<Position>>> iterator, Node<NameProperty> currentNode, Map<Position, NameProperty> errorAccumulator) {
        if(iterator.hasNext()) {
            Map.Entry<NameProperty, List<Position>> current = iterator.next();
            if(!current.getKey().equals(currentNode.value)) {
                if(currentNode.hasParent(current.getKey())) {
                    checkIsInSameTreePath(iterator, currentNode, errorAccumulator);
                } else {
                    Node<NameProperty> child = currentNode.getChildrenWithValueOrNull(current.getKey());
                    if(child == null) {
                        errorAccumulator.put(new MultiplePosition(current.getValue()), current.getKey());
                    } else {
                        checkIsInSameTreePath(iterator, child, errorAccumulator);
                    }
                }
            } else {
                checkIsInSameTreePath(iterator, currentNode, errorAccumulator);
            }
        }
        return currentNode.value;
    }

    private void addToMap(Map<Modifiers, Map<NameProperty, List<Position>>> map, NodeList<Modifier> modifierList, Position position, String name) {
        Modifiers modifiers = new Modifiers();
        modifierList.forEach(e -> modifiers.modifiers.add(e.getKeyword()));
        NameProperty nameProperty = new NameProperty(name);
        if(map.containsKey(modifiers)) {
            map.replace(modifiers, addToNameProperty(map.get(modifiers), nameProperty, position));
        } else {
            map.put(modifiers, addToNameProperty(new HashMap<>(), nameProperty, position));
        }
    }

    private Map<NameProperty, List<Position>> addToNameProperty(Map<NameProperty, List<Position>> map, NameProperty nameProperty, Position position) {
        if(map.containsKey(nameProperty)) {
            map.get(nameProperty).add(position);
        } else {
            List<Position> list = new ArrayList<>();
            list.add(position);
            map.put(nameProperty, list);
        }
        return map;
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
            Modifiers that = (Modifiers) o;
            return Objects.equals(this.modifiers, that.modifiers);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(modifiers);
        }

        @Override
        public String toString() {
            return modifiers.toString();
        }
    }
}