package antlr.ast.node.statement;

import antlr.ast.node.ASTNode;
import antlr.ast.visitor.ASTVisitor;

public class WhileStatement extends ASTNode {
    private final ASTNode condition;  // The loop condition expression
    private final ASTNode body;       // The body of the while loop
    private final ASTNode elseBody;   // Optional 'else' body (for Python-style while-else)

    public WhileStatement(String nodeType, int startLine, int startChar, int endLine, int endChar, ASTNode condition, ASTNode body, ASTNode elseBody) {
        super(nodeType, startLine, startChar, endLine, endChar);
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public ASTNode getBody() {
        return body;
    }

    public ASTNode getElseBody() {
        return elseBody;
    }

    public String toString() {
        return "WhileStatement{" +
                "condition=" + condition +
                ", body=" + body +
                ", elseBody=" + elseBody +
                '}';
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}