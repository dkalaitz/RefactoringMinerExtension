package antlr.ast.node.misc;

import antlr.ast.visitor.ASTVisitor;
import antlr.ast.node.ASTNode;

public class Identifier extends ASTNode {
    private final String name;

    public Identifier(String name, int startLine, int startChar, int endLine, int endChar) {
        super("Identifier", startLine, startChar, endLine, endChar);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Identifier{" +
                "name='" + name + '\'' +
                '}';
    }
}
