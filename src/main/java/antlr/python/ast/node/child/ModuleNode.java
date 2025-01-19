package antlr.python.ast.node.child;

import antlr.python.ast.node.ASTNode;
import antlr.python.ast.visitor.PythonASTVisitor;

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

    @Override
    public String toString() {
        return "ModuleNode{" +
                "body=" + body +
                '}';
    }
}
