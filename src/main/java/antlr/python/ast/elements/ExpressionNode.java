package antlr.python.ast.elements;

import antlr.python.ast.PythonASTVisitor;

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
