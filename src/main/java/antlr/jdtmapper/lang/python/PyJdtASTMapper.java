package antlr.jdtmapper.lang.python;

import antlr.ast.builder.python.component.PyDeclarationASTBuilder;
import antlr.ast.node.LangASTNode;
import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.ast.node.expression.LangAssignment;
import antlr.ast.node.expression.LangInfixExpression;
import antlr.ast.node.expression.LangSimpleName;
import antlr.ast.node.statement.LangBlock;
import antlr.ast.node.statement.LangReturnStatement;
import antlr.ast.node.unit.LangCompilationUnit;
import antlr.jdtmapper.BaseJdtASTMapper;
import antlr.jdtmapper.lang.python.submapper.*;
import org.eclipse.jdt.core.dom.*;

/**
 * Maps a custom Python AST node to a JDT-compatible AST node.
 * Python is dynamically typed, so we map all variables to Object type.
 */
public class PyJdtASTMapper extends BaseJdtASTMapper {

    private final PyCompilationUnitMapper pyCompilationUnitMapper = new PyCompilationUnitMapper();
    private final PyDeclarationMapper pyDeclarationMapper = new PyDeclarationMapper();
    private final PyExpressionMapper pyExpressionMapper = new PyExpressionMapper();
    private final PyStatementMapper pyStatementMapper = new PyStatementMapper();
    private final PyLiteralMapper pyLiteralMapper = new PyLiteralMapper();

    public PyJdtASTMapper() {}

    @Override
    public CompilationUnit mapCompilationUnit(LangCompilationUnit langCompilationUnit, AST jdtAst) {
        return pyCompilationUnitMapper.mapCompilationUnit(langCompilationUnit, jdtAst, this);
    }

    @Override
    public TypeDeclaration mapTypeDeclaration(LangTypeDeclaration langTypeDeclaration, AST jdtAst) {
        return pyDeclarationMapper.mapTypeDeclaration(langTypeDeclaration, jdtAst, this);
    }

    @Override
    public MethodDeclaration mapMethodDeclaration(LangMethodDeclaration langMethodDeclaration, AST jdtAst) {
        return pyDeclarationMapper.mapMethodDeclaration(langMethodDeclaration, jdtAst, this);
    }

    @Override
    public SingleVariableDeclaration mapSingleVariableDeclaration(LangSingleVariableDeclaration langVar, AST jdtAst) {
        return pyDeclarationMapper.mapSingleVariableDeclaration(langVar, jdtAst, this);
    }

    public Assignment mapAssignment(LangAssignment langAssignment, AST jdtAst) {
        return pyExpressionMapper.mapAssignment(langAssignment, jdtAst, this);
    }

    public SimpleName mapSimpleName(LangSimpleName langSimpleName, AST jdtAst) {
        return pyExpressionMapper.mapSimpleName(langSimpleName, jdtAst, this);
    }

    @Override
    public Block mapBlock(LangBlock langBlock, AST jdtAst) {
        return pyStatementMapper.mapBlock(langBlock, jdtAst, this);
    }

    @Override
    public InfixExpression mapInfixExpression(LangInfixExpression langInfixExpression, AST jdtAst) {
        return pyExpressionMapper.mapInfixExpression(langInfixExpression, jdtAst, this);
    }

    @Override
    public ReturnStatement mapReturnStatement(LangReturnStatement langReturnStatement, AST jdtAst) {
        return pyStatementMapper.mapReturnStatement(langReturnStatement, jdtAst, this);
    }

    // Add this to your PyJdtASTMapper class
    private void debugSourceRanges(ASTNode node, LangASTNode langNode) {
        System.out.println("Node: " + node.getClass().getSimpleName() +
                " Position: " + node.getStartPosition() +
                " Length: " + node.getLength() +
                " From LangNode: " + langNode.getClass().getSimpleName() +
                " Start: " + langNode.getStartChar() +
                " Length: " + langNode.getLength());
    }

}