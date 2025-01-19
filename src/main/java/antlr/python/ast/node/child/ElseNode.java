package antlr.python.ast.node.child;


import antlr.python.ast.node.ASTNode;
import antlr.python.ast.visitor.PythonASTVisitor;

import java.util.List;

// Node representing an "else" block in a for/while loop
public class ElseNode extends ASTNode {
    public List<ASTNode> body;

    public ElseNode(List<ASTNode> body) {
        this.body = body;
    }

    @Override
    public void accept(PythonASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ElseNode{" +
                "body=" + body +
                '}';
    }
}

