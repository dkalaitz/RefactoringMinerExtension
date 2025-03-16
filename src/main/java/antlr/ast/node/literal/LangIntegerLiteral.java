package antlr.ast.node.literal;

import antlr.ast.node.LangASTNode;
import antlr.ast.visitor.LangASTVisitor;

public class LangIntegerLiteral extends LangASTNode {
    private int value;

    public LangIntegerLiteral(int startLine, int startChar, int endLine, int endChar, int value) {
        super("LangIntegerLiteral", startLine, startChar, endLine, endChar);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String toString() {
        return "LangIntegerLiteral{" +
                "value=" + value +
                '}';
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }
}