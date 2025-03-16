package antlr.ast.node.expression;

import antlr.ast.node.LangASTNode;
import antlr.ast.visitor.LangASTVisitor;

public class LangAssignment extends LangASTNode {
    private final String operator;
    private final LangASTNode leftSide;
    private final LangASTNode rightSide;

    public LangAssignment(String operator, LangASTNode leftSide, LangASTNode rightSide, int startLine, int startChar, int endLine, int endChar) {
        super("LangAssignment", startLine, startChar, endLine, endChar);
        this.operator = operator;
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        addChild(leftSide);
        addChild(rightSide);
    }

    public String getOperator() { return operator; }

    public LangASTNode getLeftSide() {
        return leftSide;
    }

    public LangASTNode getRightSide() {
        return rightSide;
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
        leftSide.accept(visitor);
        rightSide.accept(visitor);
    }

    public String toString() {
        return "LangAssignment{" +
                "leftSide=" + leftSide +
                ", operator=" + "'" + operator + "'"+
                ", rightSide=" + rightSide +
                '}';
    }
}
