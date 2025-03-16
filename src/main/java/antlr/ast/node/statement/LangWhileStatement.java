package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.visitor.LangASTVisitor;

public class LangWhileStatement extends LangASTNode {
    private final LangASTNode condition;  // The loop condition expression
    private final LangASTNode body;       // The body of the while loop
    private final LangASTNode elseBody;   // Optional 'else' body (for Python-style while-else)

    public LangWhileStatement(String nodeType, int startLine, int startChar, int endLine, int endChar, LangASTNode condition, LangASTNode body, LangASTNode elseBody) {
        super(nodeType, startLine, startChar, endLine, endChar);
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
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

    public String toString() {
        return "LangWhileStatement{" +
                "condition=" + condition +
                ", body=" + body +
                ", elseBody=" + elseBody +
                '}';
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

}