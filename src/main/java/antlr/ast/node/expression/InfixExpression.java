package antlr.ast.node.expression;

import antlr.ast.visitor.ASTVisitor;
import antlr.ast.node.ASTNode;

public class InfixExpression extends ASTNode {
    private final ASTNode left;
    private final String operator;
    private final ASTNode right;

    public InfixExpression(ASTNode left, String operator, ASTNode right, int startLine, int startChar, int endLine, int endChar) {
        super("InfixExpression", startLine, startChar, endLine, endChar);
        this.left = left;
        this.operator = operator;
        this.right = right;
        if (left != null) addChild(left);
        if (right != null) addChild(right);
    }

    public ASTNode getLeft() {
        return left;
    }

    public ASTNode getRight() {
        return right;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        if (left != null) left.accept(visitor);
        if (right != null) right.accept(visitor);
    }

    @Override
    public String toString() {
        return "InfixExpression{" +
                "left=" + left +
                ", operator='" + operator + '\'' +
                ", right=" + right +
                '}';
    }
}