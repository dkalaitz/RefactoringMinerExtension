package antlr.ast.node.declaration;

import antlr.ast.node.statement.LangBlock;
import antlr.ast.visitor.LangASTVisitor;
import antlr.ast.node.LangASTNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class representing a method within a type
public class LangMethodDeclaration extends LangASTNode {
    private String name;
    private final List<LangSingleVariableDeclaration> parameters = new ArrayList<>();

    private LangBlock body;

    public LangMethodDeclaration(int startLine, int startChar, int endLine, int endChar) {
        super("MethodDeclaration", startLine, startChar, endLine, endChar);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LangSingleVariableDeclaration> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public void addParameter(LangSingleVariableDeclaration langSingleVariableDeclaration) {
        parameters.add(langSingleVariableDeclaration);
    }

    public LangBlock getBody() {
        return body;
    }

    public void setBody(LangBlock body) {
        this.body = body;
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
        for (LangSingleVariableDeclaration langSingleVariableDeclaration : parameters) {
            langSingleVariableDeclaration.accept(visitor);
        }
        if (body != null) {
            body.accept(visitor);
        }
    }

    public String toString() {
        return "MethodDeclaration{" +
                "name='" + name + '\'' +
                ", parameters=" + parameters +
                ", body=" + body +
                '}';
    }
}
