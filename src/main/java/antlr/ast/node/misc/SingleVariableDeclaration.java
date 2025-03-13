package antlr.ast.node.misc;

import antlr.ast.visitor.ASTVisitor;
import antlr.ast.node.ASTNode;

/**
 * Represents a method/function parameter declaration in a JDT-like style
 */
public class SingleVariableDeclaration extends ASTNode {
    private final SimpleName simpleName;

    public SingleVariableDeclaration(SimpleName simpleName, int startLine, int startChar, int endLine, int endChar) {
        super("SingleVariableDeclaration", startLine, startChar, endLine, endChar);
        this.simpleName = simpleName;
    }

    public SimpleName getSimpleName() {
        return simpleName;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    public String toString() {
        return "SingleVariableDeclaration{" +
                "simpleName='" + simpleName + '\'' +
                '}';
    }
}
