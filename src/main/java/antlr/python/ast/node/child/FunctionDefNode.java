package antlr.python.ast.node.child;

import antlr.python.ast.node.ASTNode;
import antlr.python.ast.visitor.PythonASTVisitor;

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

    @Override
    public String toString() {
        return "FunctionDefNode{" +
                "name='" + name + '\'' +
                ", params=" + params +
                ", body=" + body +
                '}';
    }
}
