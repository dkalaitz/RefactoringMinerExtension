package antlr.ast.node.statement;

import antlr.ast.node.ASTNode;
import antlr.ast.visitor.ASTVisitor;

public class WhileStatement extends ASTNode {

    public WhileStatement(String nodeType, int startLine, int startChar, int endLine, int endChar) {
        super(nodeType, startLine, startChar, endLine, endChar);
    }

    @Override
    public void accept(ASTVisitor visitor) {

    }
}
