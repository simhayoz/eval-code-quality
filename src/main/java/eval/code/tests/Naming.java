package eval.code.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private Map<List<Modifier.ModifierKeyword>, List<TypeDeclaration>> class_names = new HashMap<>();
    private Map<List<Modifier.ModifierKeyword>, List<MethodDeclaration>> method_names = new HashMap<>();

    public Naming(CompilationUnit cu) {
        super(cu);
        NAME = "naming";
    }

    @Override
    protected void test() {
        getCU().accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeDeclaration n) {
                // Check class variable name
                checkClassVariable(n.getFields());
                addNodeToListMap(class_names, getModifiersList(n.modifiers()), n);
                return true;
            }

            @Override
            public boolean visit(MethodDeclaration node) {
                if (!node.isConstructor()) {
                    addNodeToListMap(method_names, getModifiersList(node.modifiers()), node);
                }
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
        // Check variable inside method name
        checkForCurrentGroup(method_var_names, null);
        // Check method name
        method_names.forEach((list_mod, fd) -> {
            Map<ASTNode, NameProperty> current_group = new HashMap<>();
            fd.forEach(f -> {
                current_group.put(f, NameProperty.getFor(f.getName().toString()));
            });
            checkForCurrentGroup(current_group, list_mod);
        });
        // Check class name
        class_names.forEach((list_mod, fd) -> {
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
            addNodeToListMap(modifiers_group, getModifiersList(f.modifiers()), f);
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

    private void checkForCurrentGroup(Map<ASTNode, NameProperty> group, List<Modifier.ModifierKeyword> modifiers) {
        if (group.size() > 1) {
            NameProperty pName = null;
            Set<NameProperty> property_set = new HashSet<NameProperty>(group.values());
            for (NameProperty n : property_set) {
                if (pName != null && !n.isLogicEquals(pName)) {
                    PositionList pos_list = PositionList.empty();
                    group.forEach((f, __) -> {
                        pos_list.add(Position.setPos(getLine(f), getCol(f)));
                    });
                    if (modifiers != null) {
                        addError(pos_list, "same naming convention for the same modifiers:" + modifiers,
                                property_set.toString());
                    } else {
                        addError(pos_list, "same naming convention", property_set.toString());
                    }
                } else {
                    pName = n;
                }
            }

        }
    }

    private <T extends ASTNode> void addNodeToListMap(Map<List<Modifier.ModifierKeyword>, List<T>> map,
            List<Modifier.ModifierKeyword> key, T value) {
        List<T> new_list;
        if (map.containsKey(key)) {
            new_list = map.get(key);
            new_list.add(value);
            map.replace(key, new_list);
        } else {
            new_list = new ArrayList<>();
            new_list.add(value);
            map.put(key, new_list);
        }
    }

    private List<Modifier.ModifierKeyword> getModifiersList(List<Modifier> modifiers) {
        List<Modifier.ModifierKeyword> l = new ArrayList<>();
        for (Modifier m : modifiers) {
            l.add(m.getKeyword());
        }
        return l;
    }
}