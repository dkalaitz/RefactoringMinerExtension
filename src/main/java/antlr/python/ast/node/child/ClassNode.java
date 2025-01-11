package antlr.python.ast.node.child;

import antlr.python.ast.node.ASTNode;
import antlr.python.ast.visitor.PythonASTVisitor;

import java.util.List;

public class ClassNode extends ASTNode {
    public String name;
    public List<ASTNode> body;

    public ClassNode(String name, List<ASTNode> body) {
        this.name = name;
        this.body = body;
    }

    @Override
    public void accept(PythonASTVisitor visitor) {
        visitor.visit(this);
    }
}
