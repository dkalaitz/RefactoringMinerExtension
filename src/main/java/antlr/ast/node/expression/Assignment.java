package antlr.ast.node.expression;

import antlr.ast.visitor.ASTVisitor;
import antlr.ast.node.ASTNode;

public class Assignment extends ASTNode {
    private final String operator;
    private final ASTNode leftSide;
    private final ASTNode rightSide;

    public Assignment(String operator, ASTNode leftSide, ASTNode rightSide, int startLine, int startChar, int endLine, int endChar) {
        super("Assignment", startLine, startChar, endLine, endChar);
        this.operator = operator;
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        addChild(leftSide);
        addChild(rightSide);
    }

    public String getOperator() { return operator; }

    public ASTNode getLeftSide() {
        return leftSide;
    }

    public ASTNode getRightSide() {
        return rightSide;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        leftSide.accept(visitor);
        rightSide.accept(visitor);
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "leftSide=" + leftSide +
                ", operator=" + "'" + operator + "'"+
                ", rightSide=" + rightSide +
                '}';
    }
}
