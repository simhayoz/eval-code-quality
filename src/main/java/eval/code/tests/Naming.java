package eval.code.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import eval.code.tools.NameProperty;
import eval.code.tools.pos.Position;
import eval.code.tools.pos.PositionList;

public class Naming extends CUBasedTest {

    private Map<ASTNode, NameProperty> method_var_names = new HashMap<>();

    public Naming(CompilationUnit cu) {
        super(cu);
        NAME = "naming";
    }

    @Override
    protected void test() {
        getCU().accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeDeclaration n) {
                checkClassVariable(n.getFields());
                checkClassMethod(n.getMethods());
                return true;
            }

            @Override
            public boolean visit(MethodDeclaration node) {
                node.getBody().accept(new ASTVisitor() {
                    @Override
                    public boolean visit(VariableDeclarationStatement v) {
                        VariableDeclarationFragment vd = (VariableDeclarationFragment) v.fragments().get(0);
                        method_var_names.put(v, NameProperty.getFor(vd.getName().toString()));
                        return true;
                    }
                });
                return true;
            }
        });
        checkForCurrentGroup(method_var_names, new ArrayList<>());
    }

    private void checkClassMethod(MethodDeclaration[] methods) {
        Map<List<Modifier.ModifierKeyword>, List<MethodDeclaration>> modifiers_group = new HashMap<>();
        for (MethodDeclaration m : methods) {
            if (!m.isConstructor()) {
                List<Modifier.ModifierKeyword> l = new ArrayList<>();
                for (Object o : m.modifiers()) {
                    l.add(((Modifier) o).getKeyword());
                }
                if (modifiers_group.containsKey(l)) {
                    List<MethodDeclaration> new_list = modifiers_group.get(l);
                    new_list.add(m);
                    modifiers_group.replace(l, new_list);
                } else {
                    List<MethodDeclaration> new_list = new ArrayList<>();
                    new_list.add(m);
                    modifiers_group.put(l, new_list);
                }
            }

        }
        modifiers_group.forEach((list_mod, fd) -> {
            Map<ASTNode, NameProperty> current_group = new HashMap<>();
            fd.forEach(f -> {
                current_group.put(f, NameProperty.getFor(f.getName().toString()));
            });
            checkForCurrentGroup(current_group, list_mod);
        });
    }

    private void checkClassVariable(FieldDeclaration[] fields) {
        Map<List<Modifier.ModifierKeyword>, List<FieldDeclaration>> modifiers_group = new HashMap<>();
        for (FieldDeclaration f : fields) {
            List<Modifier.ModifierKeyword> l = new ArrayList<>();
            for (Object o : f.modifiers()) {
                l.add(((Modifier) o).getKeyword());
            }
            if (modifiers_group.containsKey(l)) {
                List<FieldDeclaration> new_list = modifiers_group.get(l);
                new_list.add(f);
                modifiers_group.replace(l, new_list);
            } else {
                List<FieldDeclaration> new_list = new ArrayList<>();
                new_list.add(f);
                modifiers_group.put(l, new_list);
            }
        }
        modifiers_group.forEach((list_mod, fd) -> {
            Map<ASTNode, NameProperty> current_group = new HashMap<>();
            fd.forEach(f -> {
                VariableDeclarationFragment v = (VariableDeclarationFragment) f.fragments().get(0);
                current_group.put(f, NameProperty.getFor(v.getName().toString()));
            });
            checkForCurrentGroup(current_group, list_mod);
        });
    }

    private void checkForCurrentGroup(Map<ASTNode, NameProperty> group, List<Modifier.ModifierKeyword> modifier) {
        PositionList pos_list = PositionList.empty();
        if (group != null && group.size() > 1) {
            Map<NameProperty, List<ASTNode>> property_map = group.entrySet().stream().collect(Collectors
                    .groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
            if (property_map.size() > 1) {
                group.forEach((f, __) -> {
                    Position p = Position.setPos(getLine(f), getCol(f));
                    pos_list.add(p);
                });
                addError(pos_list, "same naming convention for the same modifiers " + modifier,
                        property_map.keySet().toString());
            }
        }
    }
}