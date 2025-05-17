package antlr.ast.node.statement;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;

import java.util.List;

/**
 * Represents a case within a switch/match statement in the AST.
 * For Python, this corresponds to a 'case' block or the 'case _' (default).
 */
public class LangCaseStatement extends LangASTNode {
    /**
     * The pattern for this case (can be null or a special value for 'default'/underscore).
     */
    private LangASTNode pattern;

    /**
     * The statements to execute if the pattern matches.
     */
    private List<LangASTNode> body;

    public LangCaseStatement(PositionInfo positionInfo, LangASTNode pattern, List<LangASTNode> body) {
        super(NodeTypeEnum.CASE_STATEMENT, positionInfo);
        this.pattern = pattern;
        this.body = body;
    }

    public LangASTNode getPattern() {
        return pattern;
    }

    public List<LangASTNode> getBody() {
        return body;
    }

    public void setPattern(LangASTNode pattern) {
        this.pattern = pattern;
    }

    public void setBody(List<LangASTNode> body) {
        this.body = body;
    }

    @Override
    public void accept(antlr.ast.visitor.LangASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "LangCaseStatement{" +
                "pattern=" + pattern +
                ", body=" + body +
                '}';
    }
}