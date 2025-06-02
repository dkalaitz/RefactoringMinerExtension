package antlr.ast.node.literal;

import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

public class LangNumberLiteral extends LangLiteral {
    private int value;

    public LangNumberLiteral() {super(NodeTypeEnum.INTEGER_LITERAL);}

    public LangNumberLiteral(PositionInfo positionInfo, int value) {
        super(NodeTypeEnum.INTEGER_LITERAL, positionInfo);
        this.value = value;
    }

    public LangNumberLiteral(int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn, int value) {
        super(NodeTypeEnum.INTEGER_LITERAL, startLine, startChar, endLine, endChar, startColumn, endColumn);
        this.value = value;
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String toString() {
        return "LangIntegerLiteral{" +
                "value=" + value +
                '}';
    }

}