package antlr.ast.node.expression;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

public class LangAwaitExpression extends LangExpression {
    private LangASTNode expression;

    public LangAwaitExpression() {
        super(NodeTypeEnum.AWAIT_EXPRESSION);
    }

    public LangAwaitExpression(PositionInfo positionInfo, LangASTNode expression) {
        super(NodeTypeEnum.AWAIT_EXPRESSION, positionInfo);
        this.expression = expression;
    }

    public LangAwaitExpression(PositionInfo positionInfo) {
        super(NodeTypeEnum.AWAIT_EXPRESSION, positionInfo);
    }

    public LangASTNode getExpression() {
        return expression;
    }

    public void setExpression(LangASTNode expression) {
        this.expression = expression;
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "LangAwaitExpression{" +
                "expression=" + expression +
                '}';
    }
}