package antlr.ast.visitor;

import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.ast.node.expression.LangAssignment;
import antlr.ast.node.expression.LangInfixExpression;
import antlr.ast.node.expression.LangMethodInvocation;
import antlr.ast.node.literal.LangBooleanLiteral;
import antlr.ast.node.literal.LangIntegerLiteral;
import antlr.ast.node.literal.LangListLiteral;
import antlr.ast.node.literal.LangStringLiteral;
import antlr.ast.node.misc.LangSimpleName;
import antlr.ast.node.statement.*;
import antlr.ast.node.unit.LangCompilationUnit;

// Visitor interface for traversing the AST
public interface LangASTVisitor {
    void visit(LangCompilationUnit langCompilationUnit);
    void visit(LangTypeDeclaration langTypeDeclaration);
    void visit(LangMethodDeclaration methodDeclaration);
    void visit(LangSingleVariableDeclaration langSingleVariableDeclaration);
    void visit(LangBlock langBlock);
    void visit(LangReturnStatement langReturnStatement);
    void visit(LangInfixExpression langInfixExpression);
    void visit(LangMethodInvocation langMethodInvocation);
    void visit(LangSimpleName langSimpleName);
    void visit(LangIfStatement langIfStatement);
    void visit(LangWhileStatement langWhileStatement);
    void visit(LangForStatement langForStatement);
    void visit(LangExpressionStatement langExpressionStatement);
    void visit(LangAssignment langAssignment);
    void visit(LangBooleanLiteral langBooleanLiteral);
    void visit(LangIntegerLiteral langIntegerLiteral);
    void visit(LangStringLiteral langStringLiteral);
    void visit(LangListLiteral langListLiteral);
}