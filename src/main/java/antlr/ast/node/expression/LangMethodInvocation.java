package antlr.ast.node.expression;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class LangMethodInvocation extends LangASTNode {
    private LangASTNode expression;
    private List<LangASTNode> arguments;

    public LangMethodInvocation() {super("LangMethodInvocation");}

    public LangMethodInvocation(PositionInfo positionInfo) {
        super("LangMethodInvocation", positionInfo);
    }

    public LangMethodInvocation(int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn) {
        super("LangMethodInvocation", startLine, startChar, endLine, endChar, startColumn, endColumn);
        this.arguments = new ArrayList<>();
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);

        // Visit the expression and all arguments
        if (expression != null) {
            expression.accept(visitor);
        }

        for (LangASTNode arg : arguments) {
            arg.accept(visitor);
        }
    }


    public void addArgument(LangASTNode argument) {
        if (this.arguments == null) {
            this.arguments = new ArrayList<>();
        }
        this.arguments.add(argument);
        addChild(argument);
    }

    public LangASTNode getExpression() {
        return expression;
    }

    public void setExpression(LangASTNode expression) {
        this.expression = expression;
        if (expression != null) {
            addChild(expression);
        }
    }

    public List<LangASTNode> getArguments() {
        return arguments;
    }

    public void setArguments(List<LangASTNode> arguments) {
        this.arguments = arguments;

        for (LangASTNode arg : arguments) {
            addChild(arg);
        }
    }

    @Override
    public String toString() {
        return "LangMethodInvocation{" +
                "expression=" + expression +
                ", arguments=" + arguments +
                '}';
    }


}