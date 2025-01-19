package antlr.python.ast.node.child;

import antlr.python.ast.node.ASTNode;
import antlr.python.ast.visitor.PythonASTVisitor;

import java.util.List;

public class WhileNode extends ASTNode {
    public final ASTNode condition;
    public final List<ASTNode> body;

    public WhileNode(ASTNode condition, List<ASTNode> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void accept(PythonASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "WhileNode{" +
                "condition=" + condition +
                ", body=" + body +
                '}';
    }
}

