package antlr.ast.node.statement;

import antlr.ast.visitor.ASTVisitor;
import antlr.ast.node.ASTNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Block extends ASTNode {
    private final List<ASTNode> statements = new ArrayList<>();

    public Block(int startLine, int startChar, int endLine, int endChar) {
        super("Block", startLine, startChar, endLine, endChar);
    }

    public List<ASTNode> getStatements() {
        return Collections.unmodifiableList(statements);
    }

    public void addStatement(ASTNode statement) {
        statements.add(statement);
        addChild(statement);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        for (ASTNode statement : statements) {
            statement.accept(visitor);
        }
    }

    @Override
    public String toString() {
        return "Block{" +
                "statements=" + statements +
                '}';
    }
}
