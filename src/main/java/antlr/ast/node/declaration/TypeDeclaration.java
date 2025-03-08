package antlr.ast.node.declaration;

import antlr.ast.visitor.ASTVisitor;
import antlr.ast.node.ASTNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class representing a type (e.g., class or interface)
public class TypeDeclaration extends ASTNode {
    private String name;
    private final List<MethodDeclaration> methods = new ArrayList<>();


    public TypeDeclaration(int startLine, int startChar, int endLine, int endChar) {
        super("TypeDeclaration", startLine, startChar, endLine, endChar);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MethodDeclaration> getMethods() {
        return Collections.unmodifiableList(methods);
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

    @Override
    public String toString() {
        return "TypeDeclaration{" +
                "name='" + name + '\'' +
                ", methods=" + methods +
                '}';
    }
}