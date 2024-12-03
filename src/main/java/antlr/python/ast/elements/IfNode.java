package antlr.python.ast.elements;

import antlr.python.ast.PythonASTVisitor;

import java.util.List;

public class IfNode extends ASTNode {
    public ASTNode condition;  // The condition expression for the if statement
    public List<ASTNode> body;  // The statements in the body of the if statement
    public List<ElifNode> elifs;  // List of elif branches
    public List<ASTNode> elseBody;  // Statements in the else block

    // Constructor for the IfNode
    public IfNode(ASTNode condition, List<ASTNode> body, List<ElifNode> elifs, List<ASTNode> elseBody) {
        this.condition = condition;
        this.body = body;
        this.elifs = elifs;
        this.elseBody = elseBody;
    }

    @Override
    public void accept(PythonASTVisitor visitor) {
        visitor.visit(this);  // Accept the visitor for traversing this node
    }
}

