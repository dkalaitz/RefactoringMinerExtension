package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PyASTBuilder;
import antlr.ast.node.LangASTNode;
import antlr.ast.node.LangASTNodeFactory;
import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.statement.LangBlock;
import antlr.base.python.Python3Parser;

import java.util.ArrayList;
import java.util.List;

public class PyDeclarationASTBuilderPy extends PyBaseASTBuilder {

    public PyDeclarationASTBuilderPy(PyASTBuilder mainBuilder) {
        super(mainBuilder);
    }

    
    public LangASTNode visitClassdef(Python3Parser.ClassdefContext ctx) {

        LangTypeDeclaration langTypeDeclaration = LangASTNodeFactory.createTypeDeclaration(ctx);

        if (ctx.block() != null) {
            // If there is a block context
            for (Python3Parser.StmtContext stmtContext : ctx.block().stmt()) {
                LangASTNode statement = mainBuilder.visit(stmtContext);

                if (statement instanceof LangMethodDeclaration) {
                    langTypeDeclaration.addMethod((LangMethodDeclaration) statement);
                }
            }
        }

        return langTypeDeclaration;
    }


    
    public LangASTNode visitFuncdef(Python3Parser.FuncdefContext ctx) {

        // Collect langSingleVariableDeclarations
        List<LangSingleVariableDeclaration> langSingleVariableDeclarations = new ArrayList<>();
        if (ctx.parameters().typedargslist() != null) {
            for (Python3Parser.TfpdefContext paramCtx : ctx.parameters().typedargslist().tfpdef()) {
                langSingleVariableDeclarations.add(LangASTNodeFactory.createSingleVariableDeclaration(paramCtx.name().getText(), paramCtx));
            }
        }

        // Visit the function body
        LangBlock body = (LangBlock) mainBuilder.visit(ctx.block());

        // Create the MethodDeclaration node using the factory
        return LangASTNodeFactory.createMethodDeclaration(ctx.name().getText(), ctx, langSingleVariableDeclarations, body);

    }

}
