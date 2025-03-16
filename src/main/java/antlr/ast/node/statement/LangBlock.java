package antlr.ast.node.statement;

import antlr.ast.visitor.LangASTVisitor;
import antlr.ast.node.LangASTNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LangBlock extends LangASTNode {
    private final List<LangASTNode> statements = new ArrayList<>();

    public LangBlock(int startLine, int startChar, int endLine, int endChar) {
        super("LangBlock", startLine, startChar, endLine, endChar);
    }

    public List<LangASTNode> getStatements() {
        return Collections.unmodifiableList(statements);
    }

    public void addStatement(LangASTNode statement) {
        statements.add(statement);
        addChild(statement);
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
        for (LangASTNode statement : statements) {
            statement.accept(visitor);
        }
    }

    public String toString() {
        return "LangBlock{" +
                "statements=" + statements +
                '}';
    }
}
