package eval.code.quality.checks;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import eval.code.quality.position.MultiplePosition;
import eval.code.quality.position.Position;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.Node;
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
        classDeclarations.forEach((modifier, map) -> checkName("class declaration", modifier, map));
        enumDeclarations.forEach((modifier, map) -> checkName("enum declaration", modifier, map));
        enumValues.forEach((modifier, map) -> checkName("enum constant", modifier, map));
        annotationDeclarations.forEach((modifier, map) -> checkName("annotation declaration", modifier, map));
        methodDeclarations.forEach((modifier, map) -> checkName("method declaration", modifier, map));
        fieldDeclarations.forEach((modifier, map) -> checkName("field declaration", modifier, map));
        variableDeclarations.forEach((modifier, map) -> checkName("variable declaration", modifier, map));
    }

    private void checkName(String type, Modifiers modifiers, Map<NameProperty, List<Position>> map) {
        if(map.size() > 1) {
            inferMapProperty.checkAndReport(getRealProperty(new ArrayList<>(map.entrySet()).iterator()),
                    "naming convention for " + type + " with " + (modifiers.modifiers.isEmpty() ? "no modifiers" : "modifiers: " + modifiers),
                    true);
        }
    }

    private Map<NameProperty, List<Position>> getRealProperty(Iterator<Map.Entry<NameProperty, List<Position>>> iterator) {
        List<Map.Entry<NameProperty, List<Position>>> acc = new ArrayList<>();
        getRealProperty(iterator, acc);
        Map<NameProperty, List<Position>> map = new HashMap<>();
        acc.forEach(el -> map.put(el.getKey(), el.getValue()));
        return map;
    }

    private void getRealProperty(Iterator<Map.Entry<NameProperty, List<Position>>> iterator, List<Map.Entry<NameProperty, List<Position>>> acc) {
        if(iterator.hasNext()) {
            Map.Entry<NameProperty, List<Position>> current = iterator.next();
            boolean wasFound = false;
            for(int i = 0; i < acc.size() && !wasFound; ++i) {
                Node<NameProperty> node = NamePropertyTree.getCurrentNodeForTree(acc.get(i).getKey());
                if(current.getKey().equals(node.value) || node.hasParent(current.getKey())) {
                    acc.get(i).getValue().addAll(current.getValue());
                    wasFound = true;
                } else {
                    Node<NameProperty> child = node.getChildrenWithValueOrNull(current.getKey());
                    if(child != null) {
                        List<Position> allPositions = new ArrayList<>();
                        allPositions.addAll(acc.get(i).getValue());
                        allPositions.addAll(current.getValue());
                        acc.remove(i);
                        acc.add(new AbstractMap.SimpleEntry<>(child.value, allPositions));
                        wasFound = true;
                    }
                }
            }
            if(!wasFound) {
                acc.add(new AbstractMap.SimpleEntry<>(current.getKey(), current.getValue()));
            }
            acc.sort(Comparator.comparingInt(entry -> -entry.getValue().size()));
            getRealProperty(iterator, acc);
        }
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

    @Override
    public String getName() {
        return "naming";
    }

    private static class Modifiers {
        public final List<Modifier.Keyword> modifiers = new ArrayList<>();

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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