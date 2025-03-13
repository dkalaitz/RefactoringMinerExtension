package antlr.ast.node.statement;

import antlr.ast.node.ASTNode;
import antlr.ast.visitor.ASTVisitor;

public class ExpressionStatement extends ASTNode {

    public ExpressionStatement(String nodeType, int startLine, int startChar, int endLine, int endChar) {
        super(nodeType, startLine, startChar, endLine, endChar);
    }

    @Override
    public void accept(ASTVisitor visitor) {

    }


    public String toString() {
        return "ExpressionStatement{}";
    }
}
