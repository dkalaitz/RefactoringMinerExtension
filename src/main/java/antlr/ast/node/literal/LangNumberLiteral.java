package antlr.ast.node.literal;

import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

public class LangNumberLiteral extends LangLiteral {
    private String value;

    public LangNumberLiteral() {super(NodeTypeEnum.INTEGER_LITERAL);}

    public LangNumberLiteral(PositionInfo positionInfo, String value) {
        super(NodeTypeEnum.INTEGER_LITERAL, positionInfo);
        this.value = value;
    }

    public LangNumberLiteral(int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn, String value) {
        super(NodeTypeEnum.INTEGER_LITERAL, startLine, startChar, endLine, endChar, startColumn, endColumn);
        this.value = value;
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return "LangIntegerLiteral{" +
                "value=" + value +
                '}';
    }

}