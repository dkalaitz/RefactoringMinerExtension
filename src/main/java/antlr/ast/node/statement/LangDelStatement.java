package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class LangDelStatement extends LangStatement {

    List<LangASTNode> targets = new ArrayList<>();

    public LangDelStatement() {
        super(NodeTypeEnum.DEL_STATEMENT);
    }

    public LangDelStatement(PositionInfo positionInfo){
        super(NodeTypeEnum.DEL_STATEMENT, positionInfo);
    }

    public LangDelStatement(PositionInfo positionInfo, List<LangASTNode> targets){
        super(NodeTypeEnum.DEL_STATEMENT, positionInfo);
        this.targets = targets;
    }


    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    public List<LangASTNode> getTargets() {
        return targets;
    }

    public void setTargets(List<LangASTNode> targets) {
        this.targets = targets;
    }

    @Override
    public String toString() {
        return "LangDelStatement{" +
                "targets=" + targets +
                '}';
    }
}
