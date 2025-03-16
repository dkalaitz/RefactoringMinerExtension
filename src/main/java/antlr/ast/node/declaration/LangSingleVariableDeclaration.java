package antlr.ast.node.declaration;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.misc.LangSimpleName;
import antlr.ast.visitor.LangASTVisitor;

/**
 * Represents a method/function parameter declaration in a JDT-like style
 */
public class LangSingleVariableDeclaration extends LangASTNode {
    private final LangSimpleName langSimpleName;

    public LangSingleVariableDeclaration(LangSimpleName langSimpleName, int startLine, int startChar, int endLine, int endChar) {
        super("LangSingleVariableDeclaration", startLine, startChar, endLine, endChar);
        this.langSimpleName = langSimpleName;
    }

    public LangSimpleName getSimpleName() {
        return langSimpleName;
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    public String toString() {
        return "LangSingleVariableDeclaration{" +
                "langSimpleName='" + langSimpleName + '\'' +
                '}';
    }
}
