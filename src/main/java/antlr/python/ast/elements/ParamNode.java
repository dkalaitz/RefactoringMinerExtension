package antlr.python.ast.elements;

import antlr.python.ast.PythonASTVisitor;

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
