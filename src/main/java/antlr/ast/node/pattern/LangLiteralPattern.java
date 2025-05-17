package antlr.ast.node.pattern;

import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

/**
 * Pattern representing a literal value (e.g., case 3:)
 */
public class LangLiteralPattern extends LangPattern {
    private final Object value;

    public LangLiteralPattern(PositionInfo positionInfo, Object value) {
        super(NodeTypeEnum.LITERAL_PATTERN, positionInfo);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public void accept(LangASTVisitor visitor) {

    }

    @Override
    public String toString() {
        return "LangLiteralPattern{value=" + value + "}";
    }
}