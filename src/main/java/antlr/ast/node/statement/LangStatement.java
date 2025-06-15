package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;

public abstract class LangStatement extends LangASTNode {

    public LangStatement(NodeTypeEnum nodeType) {
        super(nodeType);
    }

    public LangStatement(NodeTypeEnum nodeType, PositionInfo positionInfo) {
        super(nodeType, positionInfo);
    }

    public LangStatement(NodeTypeEnum nodeType, int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn) {
        super(nodeType, startLine, startChar, endLine, endChar, startColumn, endColumn);
    }
}
