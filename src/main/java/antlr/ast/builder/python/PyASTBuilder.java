package antlr.ast.builder.python;

import antlr.ast.builder.LangASTBuilder;
import antlr.ast.builder.python.component.*;
import antlr.ast.node.LangASTNode;
import antlr.base.lang.python.Python3Parser;
import antlr.base.lang.python.Python3ParserBaseVisitor;
import org.antlr.v4.runtime.TokenStream;

/**
 * Βuilder class to traverse the ANTLR parse tree
 * and build the custom AST.
 */
public class PyASTBuilder extends Python3ParserBaseVisitor<LangASTNode> implements LangASTBuilder<Python3Parser.File_inputContext> {

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

    public LangASTNode build(Python3Parser.File_inputContext ctx) { return visitFile_input(ctx); }

    /** CompilationUnit related methods **/
    @Override public LangASTNode visitFile_input(Python3Parser.File_inputContext ctx) { return compilationUnitBuilder.visitFile_input(ctx); }

    @Override public LangASTNode visitImport_stmt(Python3Parser.Import_stmtContext ctx) { return compilationUnitBuilder.visitImport_stmt(ctx); }

    /** Declaration related methods **/
    @Override public LangASTNode visitClassdef(Python3Parser.ClassdefContext ctx) { return declarationBuilder.visitClassdef(ctx); }

    @Override public LangASTNode visitFuncdef(Python3Parser.FuncdefContext ctx) { return declarationBuilder.visitFuncdef(ctx); }

    @Override public LangASTNode visitDecorated(Python3Parser.DecoratedContext ctx) { return declarationBuilder.visitDecorated(ctx); }

    /** Expression-related methods **/
    @Override public LangASTNode visitAtom(Python3Parser.AtomContext ctx) { return expressionBuilder.visitAtom(ctx); }

    @Override public LangASTNode visitAtom_expr(Python3Parser.Atom_exprContext ctx) { return expressionBuilder.visitAtom_expr(ctx); }

    @Override public LangASTNode visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) { return expressionBuilder.visitExpr_stmt(ctx); }

    @Override public LangASTNode visitExpr(Python3Parser.ExprContext ctx) { return expressionBuilder.visitExpr(ctx); }

    @Override public LangASTNode visitComparison(Python3Parser.ComparisonContext ctx){ return expressionBuilder.visitComparison(ctx); }

    /** Statement-related methods **/
    @Override public LangASTNode visitBlock(Python3Parser.BlockContext ctx) { return statementBuilder.visitBlock(ctx); }

    @Override public LangASTNode visitStmt(Python3Parser.StmtContext ctx) { return statementBuilder.visitStmt(ctx); }

    @Override public LangASTNode visitSimple_stmt(Python3Parser.Simple_stmtContext ctx) { return statementBuilder.visitSimple_stmt(ctx); }

    @Override public LangASTNode visitIf_stmt(Python3Parser.If_stmtContext ctx) { return statementBuilder.visitIf_stmt(ctx); }

    @Override public LangASTNode visitReturn_stmt(Python3Parser.Return_stmtContext ctx) { return statementBuilder.visitReturn_stmt(ctx); }

    @Override public LangASTNode visitFor_stmt(Python3Parser.For_stmtContext ctx) { return statementBuilder.visitFor_stmt(ctx); }

    @Override public LangASTNode visitWhile_stmt(Python3Parser.While_stmtContext ctx) { return statementBuilder.visitWhile_stmt(ctx); }

    @Override public LangASTNode visitBreak_stmt(Python3Parser.Break_stmtContext ctx) { return statementBuilder.visitBreak_stmt(ctx); }

    @Override public LangASTNode visitContinue_stmt(Python3Parser.Continue_stmtContext ctx) { return statementBuilder.visitContinue_stmt(ctx); }

    @Override public LangASTNode visitTry_stmt(Python3Parser.Try_stmtContext ctx) { return statementBuilder.visitTry_stmt(ctx); }

    @Override public LangASTNode visitExcept_clause(Python3Parser.Except_clauseContext ctx){ return statementBuilder.visitExcept_clause(ctx); }

    @Override public LangASTNode visitRaise_stmt(Python3Parser.Raise_stmtContext ctx) { return statementBuilder.visitRaise_stmt(ctx); }

    @Override public LangASTNode visitWith_stmt(Python3Parser.With_stmtContext ctx) { return statementBuilder.visitWith_stmt(ctx); }

    @Override public LangASTNode visitWith_item(Python3Parser.With_itemContext ctx) { return statementBuilder.visitWith_item(ctx); }

    @Override public LangASTNode visitAssert_stmt(Python3Parser.Assert_stmtContext ctx) { return statementBuilder.visitAssert_stmt(ctx); }



    // visit trailers

    // TODO
    @Override public LangASTNode visitNonlocal_stmt(Python3Parser.Nonlocal_stmtContext ctx) { return statementBuilder.visitNonlocal_stmt(ctx); }

    @Override public LangASTNode visitGlobal_stmt(Python3Parser.Global_stmtContext ctx) { return statementBuilder.visitGlobal_stmt(ctx); }

    @Override public LangASTNode visitPass_stmt(Python3Parser.Pass_stmtContext ctx) { return statementBuilder.visitPass_stmt(ctx); }

    @Override public LangASTNode visitDel_stmt(Python3Parser.Del_stmtContext ctx) { return statementBuilder.visitDel_stmt(ctx); }

    @Override public LangASTNode visitYield_stmt(Python3Parser.Yield_stmtContext ctx) { return statementBuilder.visitYield_stmt(ctx); }

    @Override public LangASTNode visitAsync_stmt(Python3Parser.Async_stmtContext ctx) { return statementBuilder.visitAsync_stmt(ctx); }

    // TODO
    @Override public LangASTNode visitMatch_stmt(Python3Parser.Match_stmtContext ctx) { return statementBuilder.visitMatch_stmt(ctx); }

//    @Override public LangASTNode visitCase_block(Python3Parser.Case_blockContext ctx){ return expressionBuilder.visitCase_block(ctx); }

    @Override public LangASTNode visitPattern(Python3Parser.PatternContext ctx){ return expressionBuilder.visitPattern(ctx); }

  //  @Override public LangASTNode visitPattern_expr(Python3Parser.As_patternContext ctx){ return expressionBuilder.visitPattern_expr(ctx); }
//    @Override public LangASTNode visitMapping_pattern(Python3Parser.Mapping_patternContext ctx){ return expressionBuilder.visitMapping_pattern(ctx); }
//
//    @Override public LangASTNode visitItems_pattern(Python3Parser.Items_patternContext ctx){ return expressionBuilder.visitItems_pattern(ctx); }
//
//    @Override public LangASTNode visitStar_pattern(Python3Parser.Star_patternContext ctx){ return expressionBuilder.visitStar_pattern(ctx); }
//
//    @Override public LangASTNode visitKey_value_pattern(Python3Parser.Key_value_patternContext ctx){ return expressionBuilder.visitKey_value_pattern(ctx); }
    @Override public LangASTNode visitLambdef(Python3Parser.LambdefContext ctx) { return statementBuilder.visitLambdadef(ctx); }

    @Override public LangASTNode visitVfpdef(Python3Parser.VfpdefContext ctx) { return statementBuilder.visitVfpdef(ctx); }
}
