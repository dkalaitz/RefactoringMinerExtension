package antlr.ast.builder.python;

import antlr.ast.builder.python.component.PyCompilationUnitASTBuilder;
import antlr.ast.builder.python.component.PyDeclarationASTBuilder;
import antlr.ast.builder.python.component.PyExpressionASTBuilder;
import antlr.ast.builder.python.component.PyStatementASTBuilder;
import antlr.ast.node.LangASTNode;
import antlr.base.lang.python.Python3Parser;
import antlr.base.lang.python.Python3ParserBaseVisitor;

/**
 * Βuilder class to traverse the ANTLR parse tree
 * and build the custom AST.
 */
public class PyASTBuilder extends Python3ParserBaseVisitor<LangASTNode> {

    private final PyCompilationUnitASTBuilder compilationUnitBuilder;
    private final PyDeclarationASTBuilder declarationBuilder;
    private final PyExpressionASTBuilder expressionBuilder;
    private final PyStatementASTBuilder statementBuilder;

    public PyASTBuilder() {
        this.compilationUnitBuilder = new PyCompilationUnitASTBuilder(this);
        this.declarationBuilder = new PyDeclarationASTBuilder(this);
        this.expressionBuilder = new PyExpressionASTBuilder(this);
        this.statementBuilder = new PyStatementASTBuilder(this);
    }

    public LangASTNode build(Python3Parser.File_inputContext ctx) {
        return visitFile_input(ctx);
    }

    // LangCompilationUnit related methods
    @Override
    public LangASTNode visitFile_input(Python3Parser.File_inputContext ctx) { return compilationUnitBuilder.visitFile_input(ctx); }

    // Declaration related methods
    @Override
    public LangASTNode visitClassdef(Python3Parser.ClassdefContext ctx) { return declarationBuilder.visitClassdef(ctx); }

    @Override
    public LangASTNode visitFuncdef(Python3Parser.FuncdefContext ctx) {
        return declarationBuilder.visitFuncdef(ctx);
    }

    // Expression related methods
    @Override
    public LangASTNode visitAtom(Python3Parser.AtomContext ctx) {
        return expressionBuilder.visitAtom(ctx);
    }

    @Override
    public LangASTNode visitAtom_expr(Python3Parser.Atom_exprContext ctx) { return expressionBuilder.visitAtom_expr(ctx); }

    @Override
    public LangASTNode visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) { return expressionBuilder.visitExpr_stmt(ctx); }

    @Override
    public LangASTNode visitExpr(Python3Parser.ExprContext ctx) {
        return expressionBuilder.visitExpr(ctx);
    }

    @Override
    public LangASTNode visitComparison(Python3Parser.ComparisonContext ctx){ return expressionBuilder.visitComparison(ctx); }

    @Override
    public LangASTNode visitTrailer(Python3Parser.TrailerContext ctx){
        return expressionBuilder.visitTrailer(ctx);
    }

    // Statement related methods
    @Override
    public LangASTNode visitBlock(Python3Parser.BlockContext ctx) {
        return statementBuilder.visitBlock(ctx);
    }

    @Override
    public LangASTNode visitStmt(Python3Parser.StmtContext ctx) {
        return statementBuilder.visitStmt(ctx);
    }

    @Override
    public LangASTNode visitSimple_stmt(Python3Parser.Simple_stmtContext ctx) { return statementBuilder.visitSimple_stmt(ctx); }

    @Override
    public LangASTNode visitIf_stmt(Python3Parser.If_stmtContext ctx) {
        return statementBuilder.visitIf_stmt(ctx);
    }

    @Override
    public LangASTNode visitReturn_stmt(Python3Parser.Return_stmtContext ctx) { return statementBuilder.visitReturn_stmt(ctx); }

    @Override
    public LangASTNode visitFor_stmt(Python3Parser.For_stmtContext ctx) {
        return statementBuilder.visitFor_stmt(ctx);
    }

    @Override
    public LangASTNode visitWhile_stmt(Python3Parser.While_stmtContext ctx) { return statementBuilder.visitWhile_stmt(ctx); }

}
