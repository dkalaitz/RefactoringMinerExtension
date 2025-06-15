package antlr.ast.node.declaration;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;

public abstract class LangDeclaration extends LangASTNode {
    public LangDeclaration(NodeTypeEnum nodeType) {
        super(nodeType);
    }

    public LangDeclaration(NodeTypeEnum nodeType, PositionInfo positionInfo) {
        super(nodeType, positionInfo);
    }

    public LangDeclaration(NodeTypeEnum nodeType, int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn) {
        super(nodeType, startLine, startChar, endLine, endChar, startColumn, endColumn);
    }
}
