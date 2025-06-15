package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

public class LangWhileStatement extends LangStatement {
    private LangASTNode condition;
    private LangASTNode body;
    private LangASTNode elseBody;

    public LangWhileStatement(){super(NodeTypeEnum.WHILE_STATEMENT);}

    public LangWhileStatement(LangASTNode condition, LangASTNode body, LangASTNode elseBody, PositionInfo positionInfo) {
        super(NodeTypeEnum.WHILE_STATEMENT, positionInfo);
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }

    public LangWhileStatement(int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn, LangASTNode condition, LangASTNode body, LangASTNode elseBody) {
        super(NodeTypeEnum.WHILE_STATEMENT, startLine, startChar, endLine, endChar, startColumn, endColumn);
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    public LangASTNode getCondition() {
        return condition;
    }

    public void setCondition(LangASTNode condition) {
        this.condition = condition;
    }

    public LangASTNode getBody() {
        return body;
    }

    public void setBody(LangASTNode body) {
        this.body = body;
    }

    public LangASTNode getElseBody() {
        return elseBody;
    }

    public void setElseBody(LangASTNode elseBody) {
        this.elseBody = elseBody;
    }

    public String toString() {
        return "LangWhileStatement{" +
                "condition=" + condition +
                ", body=" + body +
                ", elseBody=" + elseBody +
                '}';
    }

}