package antlr.python.ast.node.child;

import antlr.python.ast.node.ASTNode;
import antlr.python.ast.visitor.PythonASTVisitor;

public class ParamNode extends ASTNode {
    public String paramName;

    public ParamNode(String paramName) {
        this.paramName = paramName;
    }

    @Override
    public void accept(PythonASTVisitor visitor) {
        visitor.visit(this);
    }

}
