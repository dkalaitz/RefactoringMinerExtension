package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

public class LangBreakStatement extends LangStatement {

    public LangBreakStatement() {
        super(NodeTypeEnum.BREAK_STATEMENT);
    }

    public LangBreakStatement(PositionInfo positionInfo) {
        super(NodeTypeEnum.BREAK_STATEMENT, positionInfo);
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "LangBreakStatement";
    }

}
