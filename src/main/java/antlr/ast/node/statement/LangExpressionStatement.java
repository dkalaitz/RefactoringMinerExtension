package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

/**
 * Represents an expression statement in the AST.
 * An expression statement wraps an expression that is executed for its side effects.
 * Example: a function call like "print(x)" or an assignment "x = 5".
 */
public class LangExpressionStatement extends LangStatement {

    private LangASTNode expression;

    public LangExpressionStatement() {super(NodeTypeEnum.EXPRESSION_STATEMENT);}

    public LangExpressionStatement(PositionInfo positionInfo) {
        super(NodeTypeEnum.EXPRESSION_STATEMENT, positionInfo);
    }

    public LangExpressionStatement(int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn) {
        super(NodeTypeEnum.EXPRESSION_STATEMENT, startLine, startChar, endLine, endChar, startColumn, endColumn);
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
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