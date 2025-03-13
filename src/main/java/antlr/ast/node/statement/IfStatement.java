package antlr.ast.node.statement;

import antlr.ast.visitor.ASTVisitor;
import antlr.ast.node.ASTNode;

public class IfStatement extends ASTNode {
    private final ASTNode condition;
    private final ASTNode body;
    private final ASTNode elseBody;

    public IfStatement(ASTNode condition, ASTNode body, ASTNode elseBody, int startLine, int startChar, int endLine, int endChar) {
        super("IfStatement", startLine, startChar, endLine, endChar);
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
        if (condition != null) addChild(condition);
        if (body != null) addChild(body);
        if (elseBody != null) addChild(elseBody);
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

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        if (condition != null) condition.accept(visitor);
        if (body != null) body.accept(visitor);
        if (elseBody != null) elseBody.accept(visitor);
    }

    public String toString() {
        return "IfStatement{" +
                "condition=" + condition +
                ", body=" + body +
                ", elseBody=" + elseBody +
                '}';
    }
}