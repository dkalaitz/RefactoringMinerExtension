package antlr.ast.node.statement;

import antlr.ast.visitor.LangASTVisitor;
import antlr.ast.node.LangASTNode;

public class LangIfStatement extends LangASTNode {
    private final LangASTNode condition;
    private final LangASTNode body;
    private final LangASTNode elseBody;

    public LangIfStatement(LangASTNode condition, LangASTNode body, LangASTNode elseBody, int startLine, int startChar, int endLine, int endChar) {
        super("LangIfStatement", startLine, startChar, endLine, endChar);
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
        if (condition != null) addChild(condition);
        if (body != null) addChild(body);
        if (elseBody != null) addChild(elseBody);
    }

    public LangASTNode getCondition() {
        return condition;
    }

    public LangASTNode getBody() {
        return body;
    }

    public LangASTNode getElseBody() {
        return elseBody;
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
        if (condition != null) condition.accept(visitor);
        if (body != null) body.accept(visitor);
        if (elseBody != null) elseBody.accept(visitor);
    }

    public String toString() {
        return "LangIfStatement{" +
                "condition=" + condition +
                ", body=" + body +
                ", elseBody=" + elseBody +
                '}';
    }
}