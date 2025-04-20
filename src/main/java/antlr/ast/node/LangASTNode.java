package antlr.ast.node;


import antlr.ast.visitor.LangASTVisitor;

import java.util.ArrayList;
import java.util.List;

public abstract class LangASTNode {

    private final String nodeType;          // Type of this node (e.g., "FunctionDeclaration", "ClassDeclaration")
    private final int startLine;            // Starting line in source code
    private final int endLine;              // Ending line in source code
    private final int startChar;            // Starting character offset
    private final int endChar;              // Ending character offset
    private final int length;
    private LangASTNode parent;           // Parent node (null for root)
    private final List<LangASTNode> children;   // List of child nodes

    public LangASTNode(String nodeType, int startLine, int startChar, int endLine, int endChar) {
        this.nodeType = nodeType;
        this.startLine = startLine;
        this.startChar = startChar;
        this.endChar = endChar + 1;
        this.endLine = endLine;
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

    public List<LangASTNode> getChildren() { return children; }

    public LangASTNode getParent() { return parent; }

    public void setParent(LangASTNode parent) { this.parent = parent; }

    public String getNodeType() { return nodeType; }

    public int getStartLine() { return startLine; }

    public int getStartChar() { return startChar; }

    public int getEndLine() { return endLine; }

    public int getEndChar() { return endChar; }

    public int getLength() { return length; }

    @Override
    public abstract String toString();

    // Accept method for visitor pattern
    public abstract void accept(LangASTVisitor visitor);
}