package antlr.python.ast.node;


import antlr.python.ast.visitor.PythonASTVisitor;

import java.util.ArrayList;
import java.util.List;

public abstract class ASTNode {
    public List<ASTNode> children = new ArrayList<>();
    public abstract void accept(PythonASTVisitor visitor);
}