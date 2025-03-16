package antlr.ast.node.declaration;

import antlr.ast.node.LangASTNode;
import antlr.ast.visitor.LangASTVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class representing a type (e.g., class or interface)
public class LangTypeDeclaration extends LangASTNode {
    private String name;
    private final List<LangMethodDeclaration> methods = new ArrayList<>();


    public LangTypeDeclaration(int startLine, int startChar, int endLine, int endChar) {
        super("LangTypeDeclaration", startLine, startChar, endLine, endChar);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LangMethodDeclaration> getMethods() {
        return Collections.unmodifiableList(methods);
    }

    public void addMethod(LangMethodDeclaration method) {
        methods.add(method);
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
        for (LangMethodDeclaration method : methods) {
            method.accept(visitor);
        }
    }

    public String toString() {
        return "LangTypeDeclaration{" +
                "name='" + name + '\'' +
                ", methods=" + methods +
                '}';
    }
}