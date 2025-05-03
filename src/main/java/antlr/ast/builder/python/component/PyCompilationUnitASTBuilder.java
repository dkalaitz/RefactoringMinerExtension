package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PyASTBuilder;
import antlr.ast.node.LangASTNode;
import antlr.ast.node.LangASTNodeFactory;
import antlr.ast.node.comment.LangComment;
import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.ast.node.unit.LangCompilationUnit;
import antlr.base.lang.python.Python3Lexer;
import antlr.base.lang.python.Python3Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

public class PyCompilationUnitASTBuilder extends PyBaseASTBuilder {

    public PyCompilationUnitASTBuilder(PyASTBuilder mainBuilder) {
        super(mainBuilder);
    }

    public LangASTNode visitFile_input(Python3Parser.File_inputContext ctx) {
        LangCompilationUnit compilationUnit = LangASTNodeFactory.createCompilationUnit(ctx);

        // TODO: Handle module name

        // Process each statement in the file
        for (Python3Parser.StmtContext stmtCtx : ctx.stmt()) {
            LangASTNode stmt = mainBuilder.visit(stmtCtx);

            if (stmt == null) continue;

            // Sort statements by type
            if (stmt instanceof LangTypeDeclaration) {
                compilationUnit.addType((LangTypeDeclaration) stmt);
            } else if (stmt instanceof LangMethodDeclaration) {
                compilationUnit.addMethod((LangMethodDeclaration) stmt);
            } else if (stmt instanceof LangComment){
                compilationUnit.addComment((LangComment) stmt);
            } else {
                compilationUnit.addStatement(stmt);
            }
        }

        return compilationUnit;
    }

    // TODO: Handle comments
    // TODO: Handle imports

}
