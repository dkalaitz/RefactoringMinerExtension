package antlr.python.ast.elements;

import antlr.python.ast.PythonASTVisitor;

import java.util.List;

// Class for representing a function
public class FunctionDefNode extends ASTNode {
    public String name;
    public List<ASTNode> params;
    public List<ASTNode> body;

    public FunctionDefNode(String name, List<ASTNode> params, List<ASTNode> body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public void accept(PythonASTVisitor visitor) {
        visitor.visit(this);
    }
}
