package antlr.ast.visitor;

import antlr.ast.node.ASTNode;
import antlr.ast.node.declaration.MethodDeclaration;
import antlr.ast.node.declaration.TypeDeclaration;
import antlr.ast.node.expression.Assignment;
import antlr.ast.node.expression.InfixExpression;
import antlr.ast.node.misc.SimpleName;
import antlr.ast.node.misc.SingleVariableDeclaration;
import antlr.ast.node.statement.*;
import antlr.ast.node.unit.CompilationUnit;

// Concrete visitor to print the AST structure
public class ASTPrinter implements ASTVisitor {
    private int indentation = 0;

    private void printIndented(String message) {
        System.out.println("  ".repeat(indentation) + message);
    }

    @Override
    public void visit(CompilationUnit compilationUnit) {
        printIndented("CompilationUnit");
        indentation++;
    }

    @Override
    public void visit(TypeDeclaration typeDeclaration) {
        printIndented("TypeDeclaration: " + typeDeclaration.getName());
        indentation++;
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        printIndented("MethodDeclaration: " + methodDeclaration.getName());
        indentation++;
    }

    @Override
    public void visit(SingleVariableDeclaration singleVariableDeclaration) {
        printIndented("SingleVariableDeclaration: " + singleVariableDeclaration.getSimpleName());
    }

    @Override
    public void visit(Block node) {
        printIndented("Block");
        indentation++;

        for (ASTNode statement : node.getStatements()) {
            statement.accept(this);  // Visiting each statement in the block
        }

        indentation--;
    }

    @Override
    public void visit(ReturnStatement returnStatement) {
        printIndented("ReturnStatement");
        indentation++;
        if (returnStatement.getExpression() != null) {
            returnStatement.getExpression().accept(this);
        }
        indentation--;
    }

    @Override
    public void visit(InfixExpression infixExpression) {
        printIndented("InfixExpression: " + infixExpression.getOperator());
        indentation++;
        infixExpression.getLeft().accept(this);
        infixExpression.getRight().accept(this);
        indentation--;
    }

    @Override
    public void visit(SimpleName simpleName) {
        printIndented("SimpleName: " + simpleName.getIdentifier());
    }

    @Override
    public void visit(IfStatement node) {
        printIndented("IfStatement");
        indentation++;

        if (node.getCondition() != null) {
            printIndented("Condition:");
            indentation++;
            node.getCondition().accept(this);
            indentation--;
        }

        if (node.getElseBody() != null) {
            printIndented("ElseBlock:");
            indentation++;
            node.getElseBody().accept(this);
            indentation--;
        }

        indentation--;
    }


    @Override
    public void visit(ExpressionStatement node) {
    }


    @Override
    public void visit(ForStatement node) {

    }

    @Override
    public void visit(WhileStatement node) {

    }

    @Override
    public void visit(Assignment assignment) {
        printIndented("Assignment");
        indentation++;

        if (assignment.getLeftSide() != null) {
            printIndented("Left:");
            indentation++;
            assignment.getLeftSide().accept(this);
            indentation--;
        }

        if (assignment.getRightSide() != null) {
            printIndented("Right:");
            indentation++;
            assignment.getRightSide().accept(this);
            indentation--;
        }

        indentation--; // End operator group
        indentation--;
    }


    // Reduce indentation when leaving a node
    public void postVisit(ASTNode node) {
        indentation--;
    }
}