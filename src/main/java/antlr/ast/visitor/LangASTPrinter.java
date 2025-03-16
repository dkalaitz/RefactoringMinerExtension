package antlr.ast.visitor;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.declaration.*;
import antlr.ast.node.expression.*;
import antlr.ast.node.literal.*;
import antlr.ast.node.misc.LangSimpleName;
import antlr.ast.node.statement.*;
import antlr.ast.node.unit.LangCompilationUnit;

/**
 * AST printer implementation that creates a human-readable tree representation
 * of the abstract syntax tree with proper indentation.
 */
public class LangASTPrinter implements LangASTVisitor {
    private int indentLevel = 0;
    private final StringBuilder sb = new StringBuilder();

    /**
     * Print the entire AST starting from the provided root node.
     *
     * @param root The root node of the AST to print
     * @return A formatted string representation of the AST
     */
    public String print(LangASTNode root) {
        sb.setLength(0);
        indentLevel = 0;
        root.accept(this);
        return sb.toString();
    }

    // Helper method to create proper indentation
    private String getIndent() {
        return "  ".repeat(indentLevel);
    }

    // Helper method to append a node with its location info
    private void appendNodeInfo(LangASTNode node, String additionalInfo) {
        sb.append(getIndent())
                .append(node.getNodeType())
                .append(" [")
                .append(node.getStartLine())
                .append(":")
                .append(node.getStartChar())
                .append("-")
                .append(node.getEndLine())
                .append(":")
                .append(node.getEndChar())
                .append("]");

        if (additionalInfo != null && !additionalInfo.isEmpty()) {
            sb.append(" ").append(additionalInfo);
        }

        sb.append("\n");
    }

    @Override
    public void visit(LangCompilationUnit node) {
        appendNodeInfo(node, "types: " + node.getTypes().size());
        indentLevel++;
        for (LangTypeDeclaration type : node.getTypes()) {
            type.accept(this);
        }
        indentLevel--;
    }

    @Override
    public void visit(LangTypeDeclaration node) {
        appendNodeInfo(node, "name: " + node.getName());
        indentLevel++;
    }

    @Override
    public void visit(LangMethodDeclaration node) {
        appendNodeInfo(node, "name: " + node.getName());
        indentLevel++;
    }

    @Override
    public void visit(LangSingleVariableDeclaration node) {
        appendNodeInfo(node, "parameter: " + node.getSimpleName().getIdentifier());
    }

    @Override
    public void visit(LangBlock node) {
        appendNodeInfo(node, "statements: " + node.getStatements().size());
        indentLevel++;
    }

    @Override
    public void visit(LangIfStatement node) {
        appendNodeInfo(node, "if statement");
        indentLevel++;
        sb.append(getIndent()).append("Condition:\n");
        indentLevel++;
    }

    @Override
    public void visit(LangWhileStatement node) {
        appendNodeInfo(node, "while statement");
        indentLevel++;
        sb.append(getIndent()).append("Condition:\n");
        indentLevel++;
    }

    @Override
    public void visit(LangForStatement node) {
        appendNodeInfo(node, "for statement");
        indentLevel++;
    }

    @Override
    public void visit(LangReturnStatement node) {
        appendNodeInfo(node, "return");
        indentLevel++;
    }

    @Override
    public void visit(LangExpressionStatement node) {
        appendNodeInfo(node, "expression");
        indentLevel++;
    }

    @Override
    public void visit(LangInfixExpression node) {
        appendNodeInfo(node, "operator: '" + node.getOperator() + "'");
        indentLevel++;
        sb.append(getIndent()).append("Left:\n");
        indentLevel++;
        if (node.getLeft() != null) {
            node.getLeft().accept(this);
        }
        indentLevel--;
        sb.append(getIndent()).append("Right:\n");
        indentLevel++;
        if (node.getRight() != null) {
            node.getRight().accept(this);
        }
        indentLevel--;
    }

    @Override
    public void visit(LangAssignment node) {
        appendNodeInfo(node, "operator: '" + node.getOperator() + "'");
        indentLevel++;
        sb.append(getIndent()).append("Left side:\n");
        indentLevel++;
        if (node.getLeftSide() != null) {
            node.getLeftSide().accept(this);
        }
        indentLevel--;
        sb.append(getIndent()).append("Right side:\n");
        indentLevel++;
        if (node.getRightSide() != null) {
            node.getRightSide().accept(this);
        }
        indentLevel--;
    }

    @Override
    public void visit(LangMethodInvocation node) {
        StringBuilder args = new StringBuilder();
        args.append("args(").append(node.getArguments().size()).append(")");

        appendNodeInfo(node, args.toString());
        indentLevel++;

        sb.append(getIndent()).append("Expression:\n");
        indentLevel++;
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
        }
        indentLevel--;

        if (!node.getArguments().isEmpty()) {
            sb.append(getIndent()).append("Arguments:\n");
            indentLevel++;
            for (LangASTNode arg : node.getArguments()) {
                arg.accept(this);
            }
            indentLevel--;
        }
    }

    @Override
    public void visit(LangSimpleName node) {
        appendNodeInfo(node, "identifier: '" + node.getIdentifier() + "'");
    }

    @Override
    public void visit(LangStringLiteral node) {
        appendNodeInfo(node, "value: \"" + node.getValue() + "\"");
    }

    @Override
    public void visit(LangIntegerLiteral node) {
        appendNodeInfo(node, "value: " + node.getValue());
    }

    @Override
    public void visit(LangBooleanLiteral node) {
        appendNodeInfo(node, "value: " + node.getValue());
    }

    @Override
    public void visit(LangListLiteral node) {
        appendNodeInfo(node, "elements: " + node.getElements().size());
        indentLevel++;
        for (int i = 0; i < node.getElements().size(); i++) {
            sb.append(getIndent()).append("Element[").append(i).append("]:\n");
            indentLevel++;
            node.getElements().get(i).accept(this);
            indentLevel--;
        }
    }
}