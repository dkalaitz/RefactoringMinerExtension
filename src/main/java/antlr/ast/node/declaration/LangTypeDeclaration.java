package antlr.ast.node.declaration;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.node.VisibilityEnum;
import antlr.ast.visitor.LangASTVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class representing a type (e.g., class or interface)
public class LangTypeDeclaration extends LangASTNode {
    private String name;
    private List<LangMethodDeclaration> methods = new ArrayList<>();
    private VisibilityEnum visibility = VisibilityEnum.PUBLIC;
    private boolean isAbstract = false;

    public LangTypeDeclaration() {super(NodeTypeEnum.TYPE_DECLARATION);}

    public LangTypeDeclaration(PositionInfo positionInfo) {
        super(NodeTypeEnum.TYPE_DECLARATION, positionInfo);
    }

    public LangTypeDeclaration(int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn) {
        super(NodeTypeEnum.TYPE_DECLARATION, startLine, startChar, endLine, endChar, startColumn, endColumn);
    }

    public void addMethod(LangMethodDeclaration method) {
        methods.add(method);
        addChild(method);
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

    public VisibilityEnum getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityEnum visibility) {
        this.visibility = visibility;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public String toString() {
        return "LangTypeDeclaration{" +
                "name='" + name + '\'' +
                ", methods=" + methods +
                '}';
    }
}