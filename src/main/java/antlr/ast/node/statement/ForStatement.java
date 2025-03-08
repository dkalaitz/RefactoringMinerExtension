package antlr.ast.node.statement;

import antlr.ast.visitor.ASTVisitor;
import antlr.ast.node.ASTNode;

import java.util.List;

public class ForStatement extends ASTNode {
    private final List<ASTNode> loopVariables;
    private final ASTNode loopBody;
    private final ASTNode elseBody;

    public ForStatement(List<ASTNode> loopVariables, ASTNode loopBody, ASTNode elseBody, int startLine, int startChar, int endLine, int endChar) {
        super("ForStatement", startLine, startChar, endLine, endChar);
        this.loopVariables = loopVariables;
        this.loopBody = loopBody;
        this.elseBody = elseBody;
        if (loopVariables != null) loopVariables.forEach(this::addChild);
        if (loopBody != null) addChild(loopBody);
        if (elseBody != null) addChild(elseBody);
    }

    public List<ASTNode> getLoopVariables() {
        return loopVariables;
    }

    public ASTNode getLoopBody() {
        return loopBody;
    }

    public ASTNode getElseBody() {
        return elseBody;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        for (ASTNode variable : loopVariables) {
            variable.accept(visitor);
        }
        if (loopBody != null) loopBody.accept(visitor);
        if (elseBody != null) elseBody.accept(visitor);
    }
}