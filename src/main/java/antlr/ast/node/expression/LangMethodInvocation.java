package antlr.ast.node.expression;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class LangMethodInvocation extends LangASTNode {
    private LangASTNode expression;
    private List<LangASTNode> arguments;

    public LangMethodInvocation() {super(NodeTypeEnum.METHOD_INVOCATION);}

    public LangMethodInvocation(PositionInfo positionInfo) {
        super(NodeTypeEnum.METHOD_INVOCATION, positionInfo);
    }

    public LangMethodInvocation(int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn) {
        super(NodeTypeEnum.METHOD_INVOCATION, startLine, startChar, endLine, endChar, startColumn, endColumn);
        this.arguments = new ArrayList<>();
    }

    public String extractMethodName() {
        // Get the expression node (what's being called)
        LangASTNode expr = this.getExpression();

        if (expr == null) {
            return ""; // Handle case where expression is null
        }

        // Handle different expression types
        if (expr instanceof LangSimpleName) {
            // Simple function call like print()
            return ((LangSimpleName) expr).getIdentifier();
        }
        else if (expr instanceof LangFieldAccess fieldAccess) {
            // Method call like obj.method() or Class.method()
            return fieldAccess.getName().getIdentifier();
        }
        else if (expr instanceof LangMethodInvocation) {
            // Method chaining
            return "chain";
        }

        // Default case - extract from string representation
        String exprStr = expr.toString();
        if (exprStr.contains(".")) {
            // Return just the part after the last dot
            return exprStr.substring(exprStr.lastIndexOf(".") + 1);
        }

        // If we can't determine a more specific name, return the whole expression
        return exprStr;
    }



    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
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