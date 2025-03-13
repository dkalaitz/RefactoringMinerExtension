package antlr.ast.node.misc;

import antlr.ast.visitor.ASTVisitor;
import antlr.ast.node.ASTNode;

public class SimpleName extends ASTNode {
    private final String identifier;

    public SimpleName(String identifier, int startLine, int startChar, int endLine, int endChar) {
        super("SimpleName", startLine, startChar, endLine, endChar);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public String toString() {
        return "SimpleName{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
