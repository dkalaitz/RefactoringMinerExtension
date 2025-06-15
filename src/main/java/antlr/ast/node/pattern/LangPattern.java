package antlr.ast.node.pattern;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;

public abstract class LangPattern extends LangASTNode {
    public LangPattern(NodeTypeEnum nodeType, PositionInfo positionInfo) {
        super(nodeType, positionInfo);
    }
}
