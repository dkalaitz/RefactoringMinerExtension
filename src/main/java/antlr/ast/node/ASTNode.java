package antlr.ast.node;


import antlr.ast.visitor.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public abstract class ASTNode {

    private final String nodeType;          // Type of this node (e.g., "FunctionDeclaration", "ClassDeclaration")
    private final int startLine;            // Starting line in source code
    private final int endLine;              // Ending line in source code
    private final int startChar;            // Starting character offset
    private final int endChar;              // Ending character offset
    private ASTNode parent;           // Parent node (null for root)
    private final List<ASTNode> children;   // List of child nodes

    public ASTNode(String nodeType, int startLine, int startChar, int endLine, int endChar) {
        this.nodeType = nodeType;
        this.startLine = startLine;
        this.startChar = startChar;
        this.endLine = endLine;
        this.endChar = endChar;
        this.children = new ArrayList<>();
    }

    public void addChild(ASTNode child) {
        if (child == null) { return; }
        child.setParent(this);       // Set the parent for the child
        this.children.add(child);   // Add the child to the list
    }

    public List<ASTNode> getChildren() { return children; }

    public ASTNode getParent() { return parent; }

    public void setParent(ASTNode parent) { this.parent = parent; }

    public String getNodeType() { return nodeType; }

    public int getStartLine() { return startLine; }

    public int getStartChar() { return startChar; }

    public int getEndLine() { return endLine; }

    public int getEndChar() { return endChar; }

    // Accept method for visitor pattern
    public abstract void accept(ASTVisitor visitor);
}