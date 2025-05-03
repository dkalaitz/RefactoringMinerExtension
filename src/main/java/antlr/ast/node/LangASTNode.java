package antlr.ast.node;


import antlr.ast.node.unit.LangCompilationUnit;
import antlr.ast.visitor.LangASTVisitor;

import java.util.ArrayList;
import java.util.List;

public abstract class LangASTNode {

    private NodeTypeEnum nodeType;
    private int startLine;
    private int endLine;
    private int startOffset;
    private int endOffset;
    private int startColumn;
    private int endColumn;
    private int length;
    private LangASTNode parent;
    private List<LangASTNode> children;

    public LangASTNode(NodeTypeEnum nodeType) {
        this.nodeType = nodeType;
        this.children = new ArrayList<>();
    }

    public LangASTNode(NodeTypeEnum nodeType, PositionInfo positionInfo) {
        this.nodeType = nodeType;
        this.startLine = positionInfo.getStartLine();
        this.endLine = positionInfo.getEndLine();
        this.startOffset = positionInfo.getStartChar();
        this.endOffset = positionInfo.getEndChar() + 1;
        this.startColumn = positionInfo.getStartColumn();
        this.endColumn = positionInfo.getEndColumn();
        this.length = this.endOffset - this.startOffset;
        if (this.endOffset <= this.startOffset) {
            System.err.println("Warning: Invalid source range for " + nodeType +
                    " - start: " + this.startOffset + ", end: " + this.endOffset);
        }
        this.children = new ArrayList<>();
    }

    public LangASTNode(NodeTypeEnum nodeType, int startLine, int startOffset, int endLine, int endOffset, int startColumn, int endColumn) {
        this.nodeType = nodeType;
        this.startLine = startLine;
        this.endLine = endLine;
        this.startOffset = startOffset;
        this.endOffset = endOffset + 1;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
        this.length = this.endOffset - this.startOffset;
        if (this.endOffset <= this.startOffset) {
            System.err.println("Warning: Invalid source range for " + nodeType +
                    " - start: " + this.startOffset + ", end: " + this.endOffset);
        }
        this.children = new ArrayList<>();
    }

    public void addChild(LangASTNode child) {
        if (child == null) { return; }
        child.setParent(this);
        this.children.add(child);
    }

    public LangCompilationUnit getRootCompilationUnit() {
        LangASTNode current = this;
        while (current != null && !(current instanceof LangCompilationUnit)) {
            current = current.getParent();
        }
        return (LangCompilationUnit) current;
    }

    // Accept method for a visitor pattern
    public abstract void accept(LangASTVisitor visitor);

    public NodeTypeEnum getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeTypeEnum nodeType) {
        this.nodeType = nodeType;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public void setStartColumn(int startColumn) {
        this.startColumn = startColumn;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(int endColumn) {
        this.endColumn = endColumn;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public LangASTNode getParent() {
        return parent;
    }

    public void setParent(LangASTNode parent) {
        this.parent = parent;
    }

    public List<LangASTNode> getChildren() {
        return children;
    }

    public void setChildren(List<LangASTNode> children) {
        this.children = children;
    }

    @Override
    public abstract String toString();

}