package antlr.ast.node;


import antlr.ast.visitor.LangASTVisitor;

import java.util.ArrayList;
import java.util.List;

public abstract class LangASTNode {

    private String nodeType;
    private int startLine;
    private int endLine;
    private int startChar;
    private int endChar;
    private int startColumn;
    private int endColumn;
    private int length;
    private LangASTNode parent;
    private List<LangASTNode> children;

    public LangASTNode(String nodeType) {
        this.nodeType = nodeType;
        this.children = new ArrayList<>();
    }

    public LangASTNode(String nodeType, PositionInfo positionInfo) {
        this.nodeType = nodeType;
        this.startLine = positionInfo.getStartLine() + 1;
        this.endLine = positionInfo.getEndLine();
        this.startChar = positionInfo.getStartChar();
        this.endChar = positionInfo.getEndChar() + 1;
        this.startColumn = positionInfo.getStartColumn();
        this.endColumn = positionInfo.getEndColumn();
        this.length = this.endChar - this.startChar;
        if (this.endChar <= this.startChar) {
            System.err.println("Warning: Invalid source range for " + nodeType +
                    " - start: " + this.startChar + ", end: " + this.endChar);
        }
        this.children = new ArrayList<>();
    }

    public LangASTNode(String nodeType, int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn) {
        this.nodeType = nodeType;
        this.startLine = startLine + 1;
        this.startChar = startChar;
        this.endChar = endChar + 1;
        this.endLine = endLine;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
        this.length = this.endChar - this.startChar;
        if (this.endChar <= this.startChar) {
            System.err.println("Warning: Invalid source range for " + nodeType +
                    " - start: " + this.startChar + ", end: " + this.endChar);
        }
        this.children = new ArrayList<>();
    }

    public void addChild(LangASTNode child) {
        if (child == null) { return; }
        child.setParent(this);
        this.children.add(child);
    }

    // Accept method for visitor pattern
    public abstract void accept(LangASTVisitor visitor);

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
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

    public int getStartChar() {
        return startChar;
    }

    public void setStartChar(int startChar) {
        this.startChar = startChar;
    }

    public int getEndChar() {
        return endChar;
    }

    public void setEndChar(int endChar) {
        this.endChar = endChar;
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