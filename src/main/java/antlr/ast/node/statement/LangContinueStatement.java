package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

public class LangContinueStatement extends LangASTNode {

    public LangContinueStatement() {
        super(NodeTypeEnum.CONTINUE_STATEMENT);
    }

    public LangContinueStatement(PositionInfo positionInfo) {
        super(NodeTypeEnum.CONTINUE_STATEMENT, positionInfo);
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "LangContinueStatement";
    }
}
