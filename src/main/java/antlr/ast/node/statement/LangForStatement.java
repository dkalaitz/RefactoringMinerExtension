package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.visitor.LangASTVisitor;

import java.util.List;

public class LangForStatement extends LangStatement {

    private List<LangSingleVariableDeclaration> initializers;
    private LangASTNode condition;
    private List<LangASTNode> updates;
    private LangASTNode body;
    private LangASTNode elseBody;

    public LangForStatement() {super(NodeTypeEnum.FOR_STATEMENT);}

    public LangForStatement(List<LangSingleVariableDeclaration> initializers, LangASTNode condition, List<LangASTNode> updates,
                            LangASTNode body, LangASTNode elseBody, PositionInfo positionInfo) {
        super(NodeTypeEnum.FOR_STATEMENT, positionInfo);
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

    public LangForStatement(List<LangSingleVariableDeclaration> initializers, LangASTNode condition, List<LangASTNode> updates,
                            LangASTNode body, LangASTNode elseBody, int startLine, int startChar,
                            int endLine, int endChar, int startColumn, int endColumn) {
        super(NodeTypeEnum.FOR_STATEMENT, startLine, startChar, endLine, endChar, startColumn, endColumn);
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

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    public List<LangSingleVariableDeclaration> getInitializers() {
        return initializers;
    }

    public void setInitializers(List<LangSingleVariableDeclaration> initializers) {
        this.initializers = initializers;
    }

    public LangASTNode getCondition() {
        return condition;
    }

    public void setCondition(LangASTNode condition) {
        this.condition = condition;
    }

    public List<LangASTNode> getUpdates() {
        return updates;
    }

    public void setUpdates(List<LangASTNode> updates) {
        this.updates = updates;
    }

    public LangASTNode getBody() {
        return body;
    }

    public void setBody(LangASTNode body) {
        this.body = body;
    }

    public LangASTNode getElseBody() {
        return elseBody;
    }

    public void setElseBody(LangASTNode elseBody) {
        this.elseBody = elseBody;
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