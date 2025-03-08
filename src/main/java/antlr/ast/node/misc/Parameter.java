package antlr.ast.node.misc;

import antlr.ast.visitor.ASTVisitor;
import antlr.ast.node.ASTNode;

// Class representing a parameter of a method
public class Parameter extends ASTNode {
    private String name;

    public Parameter(String name, int startLine, int startChar, int endLine, int endChar) {
        super("Parameter", startLine, startChar, endLine, endChar);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "name='" + name + '\'' +
                '}';
    }
}
