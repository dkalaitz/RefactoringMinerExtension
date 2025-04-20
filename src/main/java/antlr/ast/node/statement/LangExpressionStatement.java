package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.visitor.LangASTVisitor;

/**
 * Represents an expression statement in the AST.
 * An expression statement wraps an expression that is executed for its side effects.
 * Example: a function call like "print(x)" or an assignment "x = 5".
 */
public class LangExpressionStatement extends LangASTNode {

    private LangASTNode expression;

    public LangExpressionStatement(int startLine, int startChar, int endLine, int endChar) {
        super("LangExpressionStatement", startLine, startChar, endLine, endChar);
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

        // Visit child expression if present
        if (expression != null) {
            expression.accept(visitor);
        }
    }

    @Override
    public String toString() {
        return "LangExpressionStatement{" +
                "expression=" + (expression != null ? expression : "null") +
                '}';
    }
}