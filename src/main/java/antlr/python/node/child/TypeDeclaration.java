package antlr.python.node.child;

import antlr.python.ast.ASTVisitor;
import antlr.python.node.ASTNode;

import java.util.ArrayList;
import java.util.List;

// Class representing a type (e.g., class or interface)
public class TypeDeclaration extends ASTNode {
    private String name;
    private List<MethodDeclaration> methods = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MethodDeclaration> getMethods() {
        return methods;
    }

    public void addMethod(MethodDeclaration method) {
        methods.add(method);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        for (MethodDeclaration method : methods) {
            method.accept(visitor);
        }
    }
}