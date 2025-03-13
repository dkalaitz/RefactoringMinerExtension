package antlr.ast.node.statement;

import antlr.ast.node.misc.SingleVariableDeclaration;
import antlr.ast.visitor.ASTVisitor;
import antlr.ast.node.ASTNode;

import java.util.List;

public class ForStatement extends ASTNode {

    private final List<SingleVariableDeclaration> initializers;
    private final ASTNode condition;
    private final List<ASTNode> updates;
    private final ASTNode body;
    private final ASTNode elseBody; // Optional (for Python-like behavior)

    public ForStatement(List<SingleVariableDeclaration> initializers, ASTNode condition, List<ASTNode> updates,
                        ASTNode body, ASTNode elseBody, int startLine, int startChar,
                        int endLine, int endChar) {
        super("ForStatement", startLine, startChar, endLine, endChar);
        this.initializers = initializers;
        this.condition = condition;
        this.updates = updates;
        this.body = body;
        this.elseBody = elseBody;

        if (initializers != null) initializers.forEach(this::addChild);
        if (condition != null) addChild(condition);
        if (updates != null) updates.forEach(this::addChild);
        if (body != null) addChild(body);
        if (elseBody != null) addChild(elseBody);
    }

    public ASTNode getElseBody() {
        return elseBody;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        if (initializers != null) {
            for (ASTNode initializer : initializers) initializer.accept(visitor);
        }
        if (condition != null) condition.accept(visitor);
        if (updates != null) {
            for (ASTNode update : updates) update.accept(visitor);
        }
        if (body != null) body.accept(visitor);
        if (elseBody != null) elseBody.accept(visitor);
    }

    @Override
    public String toString() {
        return "ForStatement{" +
                "initializers=" + initializers +
                ", condition=" + condition +
                ", updates=" + updates +
                ", body=" + body +
                ", elseBody=" + elseBody +
                '}';
    }
}