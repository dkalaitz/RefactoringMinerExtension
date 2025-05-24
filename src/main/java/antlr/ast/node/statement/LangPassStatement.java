package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

public class LangPassStatement extends LangStatement {

    public LangPassStatement() {
        super(NodeTypeEnum.PASS_STATEMENT);
    }

    public LangPassStatement(PositionInfo positionInfo){
        super(NodeTypeEnum.PASS_STATEMENT, positionInfo);
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "LangPassStatement";
    }
}
