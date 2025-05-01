package antlr.ast.node.declaration;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class representing a type (e.g., class or interface)
public class LangTypeDeclaration extends LangASTNode {
    private String name;
    private List<LangMethodDeclaration> methods = new ArrayList<>();


    public LangTypeDeclaration() {super("LangTypeDeclaration");}

    public LangTypeDeclaration(PositionInfo positionInfo) {
        super("LangTypeDeclaration", positionInfo);
    }

    public LangTypeDeclaration(int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn) {
        super("LangTypeDeclaration", startLine, startChar, endLine, endChar, startColumn, endColumn);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LangMethodDeclaration> getMethods() {
        return methods;
    }

    public void setMethods(List<LangMethodDeclaration> methods) {
        this.methods = methods;
    }

    public String toString() {
        return "LangTypeDeclaration{" +
                "name='" + name + '\'' +
                ", methods=" + methods +
                '}';
    }
}