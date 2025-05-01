package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

/**
 * Represents an expression statement in the AST.
 * An expression statement wraps an expression that is executed for its side effects.
 * Example: a function call like "print(x)" or an assignment "x = 5".
 */
public class LangExpressionStatement extends LangASTNode {

    private LangASTNode expression;

    public LangExpressionStatement() {super("LangExpressionStatement");}

    public LangExpressionStatement(PositionInfo positionInfo) {
        super("LangExpressionStatement", positionInfo);
    }

    public LangExpressionStatement(int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn) {
        super("LangExpressionStatement", startLine, startChar, endLine, endChar, startColumn, endColumn);
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);

        // Visit child expression if present
        if (expression != null) {
            expression.accept(visitor);
        }
    }

    public LangASTNode getExpression() {
        return expression;
    }

    public void setExpression(LangASTNode expression) {
        this.expression = expression;
    }


    @Override
    public String toString() {
        return "LangExpressionStatement{" +
                "expression=" + (expression != null ? expression : "null") +
                '}';
    }
}