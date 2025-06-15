package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class LangWithStatement extends LangStatement {

    private List<LangASTNode> contextItems = new ArrayList<>();
    private LangBlock body;

    public LangWithStatement() {
        super(NodeTypeEnum.WITH_STATEMENT);
    }

    public LangWithStatement(PositionInfo positionInfo) {
        super(NodeTypeEnum.WITH_STATEMENT, positionInfo);
    }

    public LangWithStatement(PositionInfo positionInfo, List<LangASTNode> contextItems, LangBlock body) {
        super(NodeTypeEnum.WITH_STATEMENT, positionInfo);
        this.contextItems = contextItems;
        this.body = body;
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    public List<LangASTNode> getContextItems() {
        return contextItems;
    }

    public void setContextItems(List<LangASTNode> contextItems) {
        this.contextItems = contextItems;
    }

    public LangBlock getBody() {
        return body;
    }

    public void setBody(LangBlock body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "LangWithStatement{" +
                "contextItems=" + contextItems +
                ", body=" + body +
                '}';
    }
}
