package antlr.ast.node.expression;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

public class LangSimpleName extends LangASTNode {
    private String identifier;

    public LangSimpleName() {super(NodeTypeEnum.SIMPLE_NAME);}

    public LangSimpleName(String identifier, PositionInfo positionInfo) {
        super(NodeTypeEnum.SIMPLE_NAME, positionInfo);
        this.identifier = identifier;
    }

    public LangSimpleName(String identifier, int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn) {
        super(NodeTypeEnum.SIMPLE_NAME, startLine, startChar, endLine, endChar, startColumn, endColumn);
        this.identifier = identifier;
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String toString() {
        return "LangSimpleName{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
