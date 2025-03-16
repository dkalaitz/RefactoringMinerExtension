package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.visitor.LangASTVisitor;

public class LangExpressionStatement extends LangASTNode {

    public LangExpressionStatement(String nodeType, int startLine, int startChar, int endLine, int endChar) {
        super(nodeType, startLine, startChar, endLine, endChar);
    }

    @Override
    public void accept(LangASTVisitor visitor) {

    }


    public String toString() {
        return "LangExpressionStatement{}";
    }
}
