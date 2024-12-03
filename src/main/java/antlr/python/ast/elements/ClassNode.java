package antlr.python.ast.elements;

import antlr.python.ast.PythonASTVisitor;

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
