package antlr.python.ast.elements;

import antlr.python.ast.PythonASTVisitor;

import java.util.List;

// Node representing a "for" loop in the AST
import java.util.List;

public class ForNode extends ASTNode {
    private List<ASTNode> iterables;  // List of iterables (e.g., the list or range being iterated over)
    private List<ASTNode> body;       // List of statements in the body of the for loop

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
}

