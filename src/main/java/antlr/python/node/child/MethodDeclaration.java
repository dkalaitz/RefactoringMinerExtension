package antlr.python.node.child;

import antlr.python.ast.ASTVisitor;
import antlr.python.node.ASTNode;

import java.util.ArrayList;
import java.util.List;

// Class representing a method within a type
public class MethodDeclaration extends ASTNode {
    private String name;
    private List<Parameter> parameters = new ArrayList<>();
    private ASTNode body;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void addParameter(Parameter parameter) {
        parameters.add(parameter);
    }

    public ASTNode getBody() {
        return body;
    }

    public void setBody(ASTNode body) {
        this.body = body;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        for (Parameter parameter : parameters) {
            parameter.accept(visitor);
        }
        if (body != null) {
            body.accept(visitor);
        }
    }
}
