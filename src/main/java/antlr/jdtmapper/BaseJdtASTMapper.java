package antlr.jdtmapper;

import antlr.ast.builder.python.PyASTBuilder;
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
import org.eclipse.jdt.core.dom.*;

public abstract class BaseJdtASTMapper implements JdtASTMapper {

    public abstract CompilationUnit mapCompilationUnit(LangCompilationUnit langCompilationUnit, AST jdtAst);
    public abstract TypeDeclaration mapTypeDeclaration(LangTypeDeclaration langTypeDeclaration, AST jdtAst);
    public abstract MethodDeclaration mapMethodDeclaration(LangMethodDeclaration langMethodDeclaration, AST jdtAst);
    public abstract SingleVariableDeclaration mapSingleVariableDeclaration(LangSingleVariableDeclaration langSingleVariableDeclaration, AST jdtAst);
    public abstract SimpleName mapSimpleName(LangSimpleName langSimpleName, AST jdtAst);
    public abstract Block mapBlock(LangBlock langBlock, AST jdtAst);
    public abstract Assignment mapAssignment(LangAssignment langAssignment, AST jdtAst);
    public abstract InfixExpression mapInfixExpression(LangInfixExpression langInfixExpression, AST jdtAst);
    public abstract ReturnStatement mapReturnStatement(LangReturnStatement langReturnStatement, AST jdtAst);

    @Override
    public ASTNode map(LangASTNode langASTNode, AST jdtAst) {
        if (langASTNode == null) {
            return null;
        }

        return switch (langASTNode.getClass().getSimpleName()) {
            case "LangCompilationUnit" ->
                    mapCompilationUnit((LangCompilationUnit) langASTNode, jdtAst);

            case "LangTypeDeclaration" ->
                    mapTypeDeclaration((LangTypeDeclaration) langASTNode, jdtAst);

            case "LangMethodDeclaration" ->
                    mapMethodDeclaration((LangMethodDeclaration) langASTNode, jdtAst);

            case "LangInfixExpression" ->
                    mapInfixExpression((LangInfixExpression) langASTNode, jdtAst);

            case "LangReturnStatement" ->
                    mapReturnStatement((LangReturnStatement) langASTNode, jdtAst);

            case "LangSimpleName" ->
                    mapSimpleName((LangSimpleName) langASTNode, jdtAst);

            case "LangBlock" ->
                    mapBlock((LangBlock) langASTNode, jdtAst);

            case "LangSingleVariableDeclaration" ->
                    mapSingleVariableDeclaration((LangSingleVariableDeclaration) langASTNode, jdtAst);

            case "LangAssignment" ->
                    mapAssignment((LangAssignment) langASTNode, jdtAst);

            default -> {
                String nodeInfo = langASTNode.getNodeType() + ": " + langASTNode;
                throw new UnsupportedOperationException(
                        "Unsupported AST node type: " + langASTNode.getClass().getSimpleName() +
                                " (" + nodeInfo + ")");
            }
        };
    }

    public static void setSourceRange(ASTNode node, LangASTNode langNode) {
        if (langNode.getStartChar() >= 0 && langNode.getLength() > 0) {
            node.setSourceRange(langNode.getStartChar(), langNode.getLength());
        }
    }

    public static void validateNodePosition(ASTNode node, LangASTNode langNode) {
        if (node.getStartPosition() < 0) {
            System.err.println("Warning: Missing source position for " +
                    node.getClass().getSimpleName() + " mapped from " +
                    langNode.getClass().getSimpleName());
        }
    }



}
