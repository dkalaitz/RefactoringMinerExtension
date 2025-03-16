package antlr.ast.node.misc;

import antlr.ast.node.LangASTNode;
import antlr.ast.visitor.LangASTVisitor;

public class LangSimpleName extends LangASTNode {
    private final String identifier;

    public LangSimpleName(String identifier, int startLine, int startChar, int endLine, int endChar) {
        super("LangSimpleName", startLine, startChar, endLine, endChar);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    public String toString() {
        return "LangSimpleName{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
