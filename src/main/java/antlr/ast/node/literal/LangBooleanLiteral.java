package antlr.ast.node.literal;

import antlr.ast.node.LangASTNode;
import antlr.ast.visitor.LangASTVisitor;

public class LangBooleanLiteral extends LangASTNode {
    private boolean value;

    public LangBooleanLiteral(int startLine, int startChar, int endLine, int endChar, boolean value) {
        super("LangBooleanLiteral", startLine, startChar, endLine, endChar);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String toString() {
        return "LangBooleanLiteral{" +
                "value=" + value +
                '}';
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }
}