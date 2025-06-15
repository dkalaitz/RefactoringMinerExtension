package antlr.ast.node.literal;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

public class LangBooleanLiteral extends LangLiteral {
    private boolean value;

    public LangBooleanLiteral() {super(NodeTypeEnum.BOOLEAN_LITERAL);}

    public LangBooleanLiteral(PositionInfo positionInfo, boolean value) {
        super(NodeTypeEnum.BOOLEAN_LITERAL, positionInfo);
        this.value = value;
    }

    public LangBooleanLiteral(int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn, boolean value) {
        super(NodeTypeEnum.BOOLEAN_LITERAL, startLine, startChar, endLine, endChar, startColumn, endColumn);
        this.value = value;
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String toString() {
        return "LangBooleanLiteral{" +
                "value=" + value +
                '}';
    }


}