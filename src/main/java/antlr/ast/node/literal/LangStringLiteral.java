package antlr.ast.node.literal;

import antlr.ast.node.LangASTNode;
import antlr.ast.visitor.LangASTVisitor;

public class LangStringLiteral extends LangASTNode {
    private String value;

    public LangStringLiteral(int startLine, int startChar, int endLine, int endChar, String value) {
        super("LangStringLiteral", startLine, startChar, endLine, endChar);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return "LangStringLiteral{" +
                "value='" + value + '\'' +
                '}';
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }
}
