package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PythonASTBuilder;
import antlr.ast.node.ASTNode;
import antlr.ast.node.ASTNodeFactory;
import antlr.ast.node.unit.CompilationUnit;
import antlr.base.python.Python3Parser;

public class CompilationUnitASTBuilder extends BasePythonASTBuilder {

    public CompilationUnitASTBuilder(PythonASTBuilder mainBuilder) {
        super(mainBuilder);
    }

    public ASTNode visitFile_input(Python3Parser.File_inputContext ctx) {
        // Create the root CompilationUnit
        CompilationUnit compilationUnit = ASTNodeFactory.createCompilationUnit(ctx);

        // Visit all statements in the file and add them to the CompilationUnit
        for (Python3Parser.StmtContext stmtCtx : ctx.stmt()) {
            ASTNode child = mainBuilder.visit(stmtCtx);
            if (child != null) {
                compilationUnit.addChild(child);
            } else {
                System.out.println("Child is null");
            }
        }
        System.out.println("CompilationUnit: " + compilationUnit.getChildren().toString());
        return compilationUnit;
    }


}
