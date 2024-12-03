package antlr.python.ast.elements;


import antlr.python.ast.PythonASTVisitor;

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
}

