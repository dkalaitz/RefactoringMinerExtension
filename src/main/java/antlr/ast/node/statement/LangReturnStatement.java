package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.visitor.LangASTVisitor;

public class LangReturnStatement extends LangASTNode {
    private LangASTNode expression;

    public LangReturnStatement(int startLine, int startChar, int endLine, int endChar) {
        super("LangReturnStatement", startLine, startChar, endLine, endChar);
    }

    public LangASTNode getExpression() {
        return expression;
    }

    public void setExpression(LangASTNode expression) {
        this.expression = expression;
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
        if (expression != null) {
            expression.accept(visitor);
        }
    }

    public String toString() {
        return "LangReturnStatement{" +
                "expression=" + expression +
                '}';
    }
}
