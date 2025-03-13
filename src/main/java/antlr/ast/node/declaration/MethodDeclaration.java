package antlr.ast.node.declaration;

import antlr.ast.node.misc.SingleVariableDeclaration;
import antlr.ast.node.statement.Block;
import antlr.ast.visitor.ASTVisitor;
import antlr.ast.node.ASTNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class representing a method within a type
public class MethodDeclaration extends ASTNode {
    private String name;
    private final List<SingleVariableDeclaration> parameters = new ArrayList<>();

    private Block body;

    public MethodDeclaration(int startLine, int startChar, int endLine, int endChar) {
        super("MethodDeclaration", startLine, startChar, endLine, endChar);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SingleVariableDeclaration> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public void addParameter(SingleVariableDeclaration singleVariableDeclaration) {
        parameters.add(singleVariableDeclaration);
    }

    public Block getBody() {
        return body;
    }

    public void setBody(Block body) {
        this.body = body;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        for (SingleVariableDeclaration singleVariableDeclaration : parameters) {
            singleVariableDeclaration.accept(visitor);
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
