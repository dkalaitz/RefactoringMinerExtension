package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PythonASTBuilder;
import antlr.ast.node.ASTNode;
import antlr.ast.node.ASTNodeFactory;
import antlr.ast.node.declaration.MethodDeclaration;
import antlr.ast.node.declaration.TypeDeclaration;
import antlr.ast.node.misc.SingleVariableDeclaration;
import antlr.ast.node.statement.Block;
import antlr.base.python.Python3Parser;

import java.util.ArrayList;
import java.util.List;

public class DeclarationASTBuilder extends BasePythonASTBuilder {

    public DeclarationASTBuilder(PythonASTBuilder mainBuilder) {
        super(mainBuilder);
    }

    
    public ASTNode visitClassdef(Python3Parser.ClassdefContext ctx) {

        TypeDeclaration typeDeclaration = ASTNodeFactory.createTypeDeclaration(ctx);

        if (ctx.block() != null) {
            // If there is a block context
            for (Python3Parser.StmtContext stmtContext : ctx.block().stmt()) {
                ASTNode statement = mainBuilder.visit(stmtContext);

                if (statement instanceof MethodDeclaration) {
                    typeDeclaration.addMethod((MethodDeclaration) statement);
                }
            }
        }

        return typeDeclaration;
    }


    
    public ASTNode visitFuncdef(Python3Parser.FuncdefContext ctx) {

        // Collect singleVariableDeclarations
        List<SingleVariableDeclaration> singleVariableDeclarations = new ArrayList<>();
        if (ctx.parameters().typedargslist() != null) {
            for (Python3Parser.TfpdefContext paramCtx : ctx.parameters().typedargslist().tfpdef()) {
                singleVariableDeclarations.add(ASTNodeFactory.createSingleVariableDeclaration(paramCtx.name().getText(), paramCtx));
            }
        }

        // Visit the function body
        Block body = (Block) mainBuilder.visit(ctx.block());

        // Create the MethodDeclaration node using the factory
        return ASTNodeFactory.createMethodDeclaration(ctx.name().getText(), ctx, singleVariableDeclarations, body);

    }

}
