package antlr.ast.node.literal;

import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

public class LangStringLiteral extends LangLiteral {
    private String value;

    public LangStringLiteral() {super(NodeTypeEnum.STRING_LITERAL);}

    public LangStringLiteral(PositionInfo positionInfo, String value) {
        super(NodeTypeEnum.STRING_LITERAL, positionInfo);
        this.value = value;
    }

    public LangStringLiteral(int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn, String value) {
        super(NodeTypeEnum.STRING_LITERAL, startLine, startChar, endLine, endChar, startColumn, endColumn);
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
        return "LangStringLiteral{" +
                "value='" + value + '\'' +
                '}';
    }

}
