package antlr.ast.node.pattern;

import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

/**
 * Pattern representing a variable binding (e.g., case x:)
 */
public class LangVariablePattern extends LangPattern {
    private final String variableName;

    public LangVariablePattern(PositionInfo positionInfo, String variableName) {
        super(NodeTypeEnum.VARIABLE_PATTERN, positionInfo);
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public void accept(LangASTVisitor visitor) {

    }

    @Override
    public String toString() {
        return "LangVariablePattern{variableName='" + variableName + "'}";
    }
}