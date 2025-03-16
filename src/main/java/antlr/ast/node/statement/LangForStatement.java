package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.visitor.LangASTVisitor;

import java.util.List;

public class LangForStatement extends LangASTNode {

    private final List<LangSingleVariableDeclaration> initializers;
    private final LangASTNode condition;
    private final List<LangASTNode> updates;
    private final LangASTNode body;
    private final LangASTNode elseBody; // Optional (for Python-like behavior)

    public LangForStatement(List<LangSingleVariableDeclaration> initializers, LangASTNode condition, List<LangASTNode> updates,
                            LangASTNode body, LangASTNode elseBody, int startLine, int startChar,
                            int endLine, int endChar) {
        super("LangForStatement", startLine, startChar, endLine, endChar);
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

    public LangASTNode getElseBody() {
        return elseBody;
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
        if (initializers != null) {
            for (LangASTNode initializer : initializers) initializer.accept(visitor);
        }
        if (condition != null) condition.accept(visitor);
        if (updates != null) {
            for (LangASTNode update : updates) update.accept(visitor);
        }
        if (body != null) body.accept(visitor);
        if (elseBody != null) elseBody.accept(visitor);
    }

    @Override
    public String toString() {
        return "LangForStatement{" +
                "initializers=" + initializers +
                ", condition=" + condition +
                ", updates=" + updates +
                ", body=" + body +
                ", elseBody=" + elseBody +
                '}';
    }
}