package antlr.python.ast.node.child;

import antlr.python.ast.node.ASTNode;
import antlr.python.ast.visitor.PythonASTVisitor;

import java.util.List;

// Node representing a "for" loop in the AST


public class ForNode extends ASTNode {
    private final List<ASTNode> iterables;  // List of iterables (e.g., the list or range being iterated over)
    private final List<ASTNode> body;       // List of statements in the body of the for loop

    // Constructor
    public ForNode(List<ASTNode> iterables, List<ASTNode> body) {
        this.iterables = iterables;
        this.body = body;
    }

    // Getter for iterables
    public List<ASTNode> getIterables() {
        return iterables;
    }

    // Getter for the body of the for loop
    public List<ASTNode> getBody() {
        return body;
    }

    // Accept method for the visitor pattern
    @Override
    public void accept(PythonASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ForNode{" +
                "iterables=" + iterables +
                ", body=" + body +
                '}';
    }
}

