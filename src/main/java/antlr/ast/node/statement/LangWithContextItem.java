package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

public class LangWithContextItem extends LangStatement {

    private LangASTNode contextExpression;
    private LangASTNode alias; // may be null


    public LangWithContextItem(){
        super(NodeTypeEnum.WITH_CONTEXT_ITEM);
    }

    public LangWithContextItem(PositionInfo positionInfo){
        super(NodeTypeEnum.WITH_CONTEXT_ITEM, positionInfo);
    }

    public LangWithContextItem(PositionInfo positionInfo, LangASTNode expr) {
        super(NodeTypeEnum.WITH_CONTEXT_ITEM, positionInfo);
        this.contextExpression = expr;
    }

    public LangWithContextItem(PositionInfo positionInfo, LangASTNode expr, LangASTNode alias) {
        super(NodeTypeEnum.WITH_CONTEXT_ITEM, positionInfo);
        this.contextExpression = expr;
        this.alias = alias;
    }


    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    public LangASTNode getContextExpression() {
        return contextExpression;
    }

    public void setContextExpression(LangASTNode contextExpression) {
        this.contextExpression = contextExpression;
    }

    public LangASTNode getAlias() {
        return alias;
    }

    public void setAlias(LangASTNode alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return "LangWithContextItem{" +
                "expression=" + contextExpression +
                ", alias=" + alias +
                '}';
    }
}
