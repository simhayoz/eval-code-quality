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

import eval.code.tools.NameProperty;
import eval.code.tools.pos.Position;

public class Naming extends CUBasedTest {

    private final List<Position> error_range = new ArrayList<>();

    public Naming(CompilationUnit cu) {
        super(cu);
        NAME = "naming";
    }

    @Override
    protected List<Position> test() {
        getCU().accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeDeclaration n) {
                checkClassVariable(n.getFields());
                checkClassMethod(n.getMethods());
                return true;
            }

            // @Override
            // public boolean visit(MethodDeclaration node) {
                
            //     return true;
            // }
        });
        List<Position> p = new ArrayList<>(error_range);
        error_range.clear();
        return p;
    }

    private void checkMethodVariable() {

    }

    private void checkClassMethod(MethodDeclaration[] methods) {
        Map<List<Modifier.ModifierKeyword>, List<MethodDeclaration>> modifiers_group = new HashMap<>();
        for (MethodDeclaration m : methods) {
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
        modifiers_group.forEach((list_mod, fd) -> {
            Map<ASTNode, NameProperty> current_group = new HashMap<>();
            fd.forEach(f -> {
                current_group.put(f, NameProperty.getFor(f.getName().toString()));
            });
            checkForCurrentGroup(current_group);
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
            checkForCurrentGroup(current_group);
        });
    }

    private void checkForCurrentGroup(Map<ASTNode, NameProperty> group) {
        List<Position> pos_list = new ArrayList<>();
        if(group != null && group.size() > 1) {
            Map<NameProperty, List<ASTNode>> property_map = group.entrySet().stream().collect(
                    Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
            if(property_map.size() > 1) {
                group.forEach((f, __) -> {
                    Position p = Position.setPos(getLine(f), getCol(f));
                    pos_list.add(p);
                    error_range.add(p);
                });
                System.out.println(property_map);
                // printError("Different convention used on lines: " + pos_list + "(used: " + property_map.keySet());
            }
        }
    }
}