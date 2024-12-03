package antlr.python.ast.elements;

import antlr.python.ast.PythonASTVisitor;

import java.util.List;

// Class for representing a Python module
public class ModuleNode extends ASTNode {
    public List<ASTNode> body;

    public ModuleNode(List<ASTNode> body) {
        this.body = body;
    }

    @Override
    public void accept(PythonASTVisitor visitor) {
        visitor.visit(this);
    }
}
