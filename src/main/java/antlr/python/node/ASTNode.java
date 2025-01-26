package antlr.python.node;


import antlr.python.ast.ASTVisitor;


// Base class for all AST nodes
public abstract class ASTNode {
    private int startPosition;
    private int endPosition;

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    // Accept method for the visitor pattern
    public abstract void accept(ASTVisitor visitor);
}