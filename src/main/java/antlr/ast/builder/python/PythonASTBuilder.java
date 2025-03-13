package antlr.ast.builder.python;

import antlr.ast.builder.python.component.*;
import antlr.ast.node.ASTNodeFactory;
import antlr.ast.node.declaration.MethodDeclaration;
import antlr.ast.node.declaration.TypeDeclaration;
import antlr.ast.node.misc.SingleVariableDeclaration;
import antlr.ast.node.statement.Block;
import antlr.ast.node.unit.CompilationUnit;
import antlr.base.python.Python3Parser;
import antlr.base.python.Python3ParserBaseVisitor;
import antlr.ast.node.ASTNode;


import java.util.ArrayList;
import java.util.List;

/**
 * Βuilder class to traverse the ANTLR parse tree
 * and build the custom AST.
 */
public class PythonASTBuilder extends Python3ParserBaseVisitor<ASTNode> {

    private final CompilationUnitASTBuilder compilationUnitBuilder;
    private final DeclarationASTBuilder declarationBuilder;
    private final ExpressionASTBuilder expressionBuilder;
    private final StatementASTBuilder statementBuilder;

    public PythonASTBuilder() {
        this.compilationUnitBuilder = new CompilationUnitASTBuilder(this);
        this.declarationBuilder = new DeclarationASTBuilder(this);
        this.expressionBuilder = new ExpressionASTBuilder(this);
        this.statementBuilder = new StatementASTBuilder(this);
    }

    public ASTNode build(Python3Parser.File_inputContext ctx) {
        return visit(ctx);
    }

    // CompilationUnit related methods
    @Override
    public ASTNode visitFile_input(Python3Parser.File_inputContext ctx) {
        return compilationUnitBuilder.visitFile_input(ctx);
    }

    // Declaration related methods
    @Override
    public ASTNode visitClassdef(Python3Parser.ClassdefContext ctx) {
        return declarationBuilder.visitClassdef(ctx);
    }

    @Override
    public ASTNode visitFuncdef(Python3Parser.FuncdefContext ctx) {
        return declarationBuilder.visitFuncdef(ctx);
    }

    // Expression related methods
    @Override
    public ASTNode visitAtom(Python3Parser.AtomContext ctx) {
        return expressionBuilder.visitAtom(ctx);
    }

    // TODO : Might not be needed
//    @Override
//    public ASTNode visitTestlist_star_expr(Python3Parser.Testlist_star_exprContext ctx) {
//        return expressionBuilder.visitTestlist_star_expr(ctx);
//    }

    @Override
    public ASTNode visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {
        return expressionBuilder.visitExpr_stmt(ctx);
    }

    @Override
    public ASTNode visitExpr(Python3Parser.ExprContext ctx) {
        return expressionBuilder.visitExpr(ctx);
    }

    // Statement related methods
    @Override
    public ASTNode visitBlock(Python3Parser.BlockContext ctx) {
        return statementBuilder.visitBlock(ctx);
    }

    @Override
    public ASTNode visitStmt(Python3Parser.StmtContext ctx) {
        return statementBuilder.visitStmt(ctx);
    }

    @Override
    public ASTNode visitSimple_stmt(Python3Parser.Simple_stmtContext ctx) {
        return statementBuilder.visitSimple_stmt(ctx);
    }

    @Override
    public ASTNode visitIf_stmt(Python3Parser.If_stmtContext ctx) {
        return statementBuilder.visitIf_stmt(ctx);
    }

    @Override
    public ASTNode visitReturn_stmt(Python3Parser.Return_stmtContext ctx) {
        return statementBuilder.visitReturn_stmt(ctx);
    }

    @Override
    public ASTNode visitFor_stmt(Python3Parser.For_stmtContext ctx) {
        return statementBuilder.visitFor_stmt(ctx);
    }

    @Override
    public ASTNode visitWhile_stmt(Python3Parser.While_stmtContext ctx) {
        return statementBuilder.visitWhile_stmt(ctx);
    }

    @Override
    public ASTNode visitComparison(Python3Parser.ComparisonContext ctx){
        return expressionBuilder.visitComparison(ctx);
    }

}
