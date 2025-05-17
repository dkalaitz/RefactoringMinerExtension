package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.node.expression.LangSimpleName;
import antlr.ast.visitor.LangASTVisitor;

public class LangWithContextItem extends LangASTNode {

    private LangASTNode expression;
    private LangSimpleName alias; // may be null


    public LangWithContextItem(){
        super(NodeTypeEnum.WITH_CONTEXT_ITEM);
    }

    public LangWithContextItem(PositionInfo positionInfo){
        super(NodeTypeEnum.WITH_CONTEXT_ITEM, positionInfo);
    }

    public LangWithContextItem(PositionInfo positionInfo, LangASTNode expr, LangSimpleName alias) {
        super(NodeTypeEnum.WITH_CONTEXT_ITEM, positionInfo);
        this.expression = expr;
        this.alias = alias;
    }


    @Override
    public void accept(LangASTVisitor visitor) {

    }

    public LangASTNode getExpression() {
        return expression;
    }

    public void setExpression(LangASTNode expression) {
        this.expression = expression;
    }

    public LangSimpleName getAlias() {
        return alias;
    }

    public void setAlias(LangSimpleName alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return "LangWithContextItem{" +
                "expression=" + expression +
                ", alias=" + alias +
                '}';
    }
}
