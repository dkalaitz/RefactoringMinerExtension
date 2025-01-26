package antlr.python.node.child;

import antlr.python.ast.ASTVisitor;
import antlr.python.node.ASTNode;

// Class representing a parameter of a method
public class Parameter extends ASTNode {
    private String name;

    public Parameter(String name) {
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

}