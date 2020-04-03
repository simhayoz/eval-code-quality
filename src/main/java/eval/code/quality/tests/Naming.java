package eval.code.quality.tests;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import eval.code.quality.position.MultiplePosition;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.NameProperty;
import eval.code.quality.utils.ReportPosition;

import java.util.*;
import java.util.stream.Collectors;

public class Naming extends CompilationUnitTest {
    private final Map<Modifiers, Map<TypeDeclaration<?>, NameProperty>> typeDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<MethodDeclaration, NameProperty>> methodDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<FieldDeclaration, NameProperty>> fieldDeclarations = new HashMap<>();
    private final Map<Modifiers, Map<VariableDeclarationExpr, NameProperty>> variableDeclarations = new HashMap<>();


    public Naming(ContentProvider contentProvider) {
        super(contentProvider);
    }

    @Override
    protected void testFor(String content, CompilationUnit compilationUnit) {
        compilationUnit.findAll(TypeDeclaration.class).forEach(type -> {
            addToMap(typeDeclarations, type.getModifiers(), type, type.getNameAsString());
        });
        compilationUnit.findAll(MethodDeclaration.class).forEach(method -> {
            addToMap(methodDeclarations, method.getModifiers(), method, method.getNameAsString());
        });
        compilationUnit.findAll(FieldDeclaration.class).forEach(field -> {
            field.getVariables().forEach(variable -> {
                addToMap(fieldDeclarations, field.getModifiers(), field, variable.getNameAsString());
            });
        });
        compilationUnit.findAll(VariableDeclarationExpr.class).forEach(variableDeclaration -> {
            variableDeclaration.getVariables().forEach(variable -> {
                addToMap(variableDeclarations, variableDeclaration.getModifiers(), variableDeclaration, variable.getNameAsString());
            });
        });
    }

    @Override
    protected void afterTests() {
        typeDeclarations.forEach(this::testName);
        methodDeclarations.forEach(this::testName);
        fieldDeclarations.forEach(this::testName);
        variableDeclarations.forEach(this::testName);
    }

    private void testName(Modifiers modifiers, Map<? extends Node, NameProperty> map) {
        if(map.size() > 1) {
            NameProperty pName = null;
            Set<NameProperty> property_set = new HashSet<>(map.values());
            for (NameProperty n : property_set) {
                if (pName != null && !n.isLogicEquals(pName)) {
                    MultiplePosition pos_list = new MultiplePosition();
                    map.forEach((f, __) -> {
                        pos_list.add(new SinglePosition(f.getBegin().get().line, f.getBegin().get().column));
                    });
                    if (modifiers != null) {
                        addError(ReportPosition.at(pos_list, "same naming convention for the same modifiers:" + modifiers,
                                property_set.toString()));
                    } else {
                        addError(ReportPosition.at(pos_list, "same naming convention", property_set.toString()));
                    }
                } else {
                    pName = n;
                }
            }
        }
    }

    private <T extends Node> void addToMap(Map<Modifiers, Map<T, NameProperty>> map, NodeList<Modifier> modifierList, T node, String name) {
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
