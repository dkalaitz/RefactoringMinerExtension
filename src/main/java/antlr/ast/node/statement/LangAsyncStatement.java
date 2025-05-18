package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

/**
 * Represents an 'async' statement or section.
 * Typically wraps another statement (like function, with, or for).
 */
public class LangAsyncStatement extends LangASTNode {

    private LangASTNode body;

    public LangAsyncStatement() {
        super(NodeTypeEnum.ASYNC_STATEMENT);
    }

    public LangAsyncStatement(PositionInfo positionInfo) {
        super(NodeTypeEnum.ASYNC_STATEMENT, positionInfo);
    }

    public LangAsyncStatement(PositionInfo positionInfo, LangASTNode body) {
        super(NodeTypeEnum.ASYNC_STATEMENT, positionInfo);
        setBody(body);
    }

    public LangASTNode getBody() {
        return body;
    }

    public void setBody(LangASTNode body) {
        this.body = body;
        if (body != null) {
            addChild(body);
        }
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "LangAsyncStatement{" +
                "body=" + body +
                '}';
    }
}