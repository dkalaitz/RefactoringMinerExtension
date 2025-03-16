package antlr.ast.node.expression;

import antlr.ast.node.LangASTNode;
import antlr.ast.visitor.LangASTVisitor;

public class LangInfixExpression extends LangASTNode {
    private final LangASTNode left;
    private final String operator;
    private final LangASTNode right;

    public LangInfixExpression(LangASTNode left, String operator, LangASTNode right, int startLine, int startChar, int endLine, int endChar) {
        super("LangInfixExpression", startLine, startChar, endLine, endChar);
        this.left = left;
        this.operator = operator;
        this.right = right;
        if (left != null) addChild(left);
        if (right != null) addChild(right);
    }

    public LangASTNode getLeft() {
        return left;
    }

    public LangASTNode getRight() {
        return right;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
        if (left != null) left.accept(visitor);
        if (right != null) right.accept(visitor);
    }

    public String toString() {
        return "LangInfixExpression{" +
                "left=" + left +
                ", operator='" + operator + '\'' +
                ", right=" + right +
                '}';
    }
}