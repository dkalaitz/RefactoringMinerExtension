package antlr.python.ast.node.child;

import antlr.python.ast.node.ASTNode;
import antlr.python.ast.visitor.PythonASTVisitor;

public class ExpressionNode extends ASTNode {
    public String expression;

    public ExpressionNode(String expression) {
        this.expression = expression;
    }

    @Override
    public void accept(PythonASTVisitor visitor) {
        visitor.visit(this);
    }
}
