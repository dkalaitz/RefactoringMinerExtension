package antlr.python.ast.node.child;

import antlr.python.ast.node.ASTNode;
import antlr.python.ast.visitor.PythonASTVisitor;

import java.util.List;

public class ElifNode extends ASTNode {
    public ASTNode condition;  // The condition expression for the elif
    public List<ASTNode> body;  // The statements in the body of the elif branch

    // Constructor for the ElifNode
    public ElifNode(ASTNode condition, List<ASTNode> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void accept(PythonASTVisitor visitor) {
        visitor.visit(this);  // Accept the visitor for traversing this node
    }
}
