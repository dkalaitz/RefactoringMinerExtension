package antlr.ast.node.statement;

import antlr.ast.visitor.ASTVisitor;
import antlr.ast.node.ASTNode;

public class ReturnStatement extends ASTNode {
    private ASTNode expression;

    public ReturnStatement(String type, int startLine, int startChar, int endLine, int endChar) {
        super(type, startLine, startChar, endLine, endChar);
    }

    public ASTNode getExpression() {
        return expression;
    }

    public void setExpression(ASTNode expression) {
        this.expression = expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        if (expression != null) {
            expression.accept(visitor);
        }
    }

    @Override
    public String toString() {
        return "ReturnStatement{" +
                "expression=" + expression +
                '}';
    }
}
