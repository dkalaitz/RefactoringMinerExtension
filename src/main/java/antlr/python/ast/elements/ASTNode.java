package antlr.python.ast.elements;


import antlr.python.ast.PythonASTVisitor;

import java.util.ArrayList;
import java.util.List;

public abstract class ASTNode {
    public List<ASTNode> children = new ArrayList<>();
    public abstract void accept(PythonASTVisitor visitor); // To allow traversal for future uses
}

// Other classes (e.g., ClassNode, ExpressionNode, etc.) would follow similarly
