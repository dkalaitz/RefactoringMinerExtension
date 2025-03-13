package antlr.ast.visitor;

import antlr.ast.node.declaration.MethodDeclaration;
import antlr.ast.node.declaration.TypeDeclaration;
import antlr.ast.node.expression.Assignment;
import antlr.ast.node.expression.InfixExpression;
import antlr.ast.node.misc.SimpleName;
import antlr.ast.node.misc.SingleVariableDeclaration;
import antlr.ast.node.statement.*;
import antlr.ast.node.unit.CompilationUnit;

// Visitor interface for traversing the AST
public interface ASTVisitor {
    void visit(CompilationUnit compilationUnit);
    void visit(TypeDeclaration typeDeclaration);
    void visit(MethodDeclaration methodDeclaration);
    void visit(SingleVariableDeclaration singleVariableDeclaration);
    void visit(Block block);
    void visit(ReturnStatement returnStatement);
    void visit(InfixExpression infixExpression);
    void visit(SimpleName simpleName);
    void visit(IfStatement ifStatement);
    void visit(WhileStatement whileStatement);
    void visit(ForStatement forStatement);
    void visit(ExpressionStatement expressionStatement);
    void visit(Assignment assignment);
}