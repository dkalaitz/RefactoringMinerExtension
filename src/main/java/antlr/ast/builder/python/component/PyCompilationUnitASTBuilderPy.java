package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PyASTBuilder;
import antlr.ast.node.LangASTNode;
import antlr.ast.node.LangASTNodeFactory;
import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.ast.node.unit.LangCompilationUnit;
import antlr.base.python.Python3Parser;

public class PyCompilationUnitASTBuilderPy extends PyBaseASTBuilder {

    public PyCompilationUnitASTBuilderPy(PyASTBuilder mainBuilder) {
        super(mainBuilder);
    }

    public LangASTNode visitFile_input(Python3Parser.File_inputContext ctx) {
        // Create the root LangCompilationUnit
        LangCompilationUnit langCompilationUnit = LangASTNodeFactory.createCompilationUnit(ctx);

        // Visit all statements in the file and add them to the LangCompilationUnit
        for (Python3Parser.StmtContext stmtCtx : ctx.stmt()) {
            LangASTNode child = mainBuilder.visit(stmtCtx);
            if (child != null) {
                langCompilationUnit.addChild(child);
                if (child instanceof LangTypeDeclaration) {
                    langCompilationUnit.addType((LangTypeDeclaration) child);
                }
            }
        }
        System.out.println("LangCompilationUnit: " + langCompilationUnit.getChildren().toString());
        return langCompilationUnit;
    }


}
