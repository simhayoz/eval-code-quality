package eval.code.quality.checks;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import eval.code.quality.position.MultiplePosition;
import eval.code.quality.position.Position;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.*;
import eval.code.quality.utils.description.DescriptionBuilder;
import eval.code.quality.utils.description.Descriptor;
import eval.code.quality.utils.naming.NameProperty;
import eval.code.quality.utils.naming.NamePropertyTree;

import java.util.*;

/**
 * Check that name for class, method, enum, variable, etc use the same naming convention for the same set of modifiers.
 */
public class Naming extends CompilationUnitCheck {
    private final Map<Modifiers, Map<NameProperty, List<Position>>> classDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<NameProperty, List<Position>>> enumDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<NameProperty, List<Position>>> enumValues = new HashMap<>();
    private final Map<Modifiers, Map<NameProperty, List<Position>>> annotationDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<NameProperty, List<Position>>> methodDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<NameProperty, List<Position>>> fieldDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<NameProperty, List<Position>>> variableDeclarations = new HashMap<>();


    public Naming(ContentProvider contentProvider) {
        super(contentProvider);
    }

    @Override
    protected void checkFor(ContentProvider contentProvider) {
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
    protected void afterChecks() {
        classDeclarations.forEach(this::checkName);
        enumDeclarations.forEach(this::checkName);
        enumValues.forEach(this::checkName);
        annotationDeclarations.forEach(this::checkName);
        methodDeclarations.forEach(this::checkName);
        fieldDeclarations.forEach(this::checkName);
        variableDeclarations.forEach(this::checkName);
    }

    private void checkName(Modifiers modifiers, Map<NameProperty, List<Position>> map) {
        if(map.size() > 1) {
            List<Map.Entry<NameProperty, List<Position>>> orderedList = new ArrayList<>(map.entrySet());
            orderedList.sort(Comparator.comparingInt(e -> -e.getValue().size()));
            List<Map.Entry<NameProperty, List<Position>>> sameSize = new ArrayList<>();
            List<Map.Entry<NameProperty, List<Position>>> smallerSize = new ArrayList<>();
            int maxSize = orderedList.get(0).getValue().size();
            orderedList.forEach(e -> (e.getValue().size() == maxSize ? sameSize : smallerSize).add(e));
            Node<NameProperty> root = NamePropertyTree.getCurrentNodeForTree(orderedList.get(0).getKey());
            if(sameSize.size() > 1) {
                Map<Position, NameProperty> accumulator = new HashMap<>();
                checkIsInSameTreePath(sameSize.iterator(), root, accumulator);
                if(!accumulator.isEmpty()) {
                    accumulator.put(getSingleOrMultiplePosition(orderedList.get(0).getValue()), orderedList.get(0).getKey());
                    DescriptionBuilder builder = new DescriptionBuilder();
                    accumulator.forEach((pos, nameProperty) -> builder.addPosition(pos, new Descriptor().setWas(nameProperty.toString())));
                    builder.setExpected(modifiers != null ? "same naming convention for the same modifiers: " + modifiers : "same naming convention");
                    addError(builder);
                }
            }
            if(!smallerSize.isEmpty()) {
                Map<Position, NameProperty> accumulator = new HashMap<>();
                NameProperty expected = checkIsInSameTreePath(smallerSize.iterator(), root, accumulator);
                accumulator.forEach((k, v) -> addError(new DescriptionBuilder()
                        .addPosition(k, new Descriptor().setExpected((modifiers != null ? "for the modifiers: " + modifiers + ": " : "") + expected).setWas(v.toString()))));
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
                        errorAccumulator.put(getSingleOrMultiplePosition(current.getValue()), current.getKey());
                    } else {
                        return checkIsInSameTreePath(iterator, child, errorAccumulator);
                    }
                }
            } else {
                return checkIsInSameTreePath(iterator, currentNode, errorAccumulator);
            }
        }
        return currentNode.value;
    }

    private void addToMap(Map<Modifiers, Map<NameProperty, List<Position>>> map, NodeList<Modifier> modifierList, Position position, String name) {
        Modifiers modifiers = new Modifiers();
        modifierList.forEach(e -> modifiers.modifiers.add(e.getKeyword()));
        // Special checks for Serializable (see https://github.com/simhayoz/eval-code-quality/issues/16)
        if(!name.equals("serialVersionUID")) {
            NameProperty nameProperty = new NameProperty(name);
            if(map.containsKey(modifiers)) {
                map.replace(modifiers, addToNameProperty(map.get(modifiers), nameProperty, position));
            } else {
                map.put(modifiers, addToNameProperty(new HashMap<>(), nameProperty, position));
            }
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

    private Position getSingleOrMultiplePosition(List<Position> positions) {
        return positions.size() == 1 ? positions.get(0) : new MultiplePosition(positions);
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