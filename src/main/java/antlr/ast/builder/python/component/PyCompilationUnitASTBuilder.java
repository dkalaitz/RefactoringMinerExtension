package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PyASTBuilder;
import antlr.ast.node.LangASTNode;
import antlr.ast.node.LangASTNodeFactory;
import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.ast.node.unit.LangCompilationUnit;
import antlr.base.lang.python.Python3Parser;

public class PyCompilationUnitASTBuilder extends PyBaseASTBuilder {

    public PyCompilationUnitASTBuilder(PyASTBuilder mainBuilder) {
        super(mainBuilder);
    }

    public LangASTNode visitFile_input(Python3Parser.File_inputContext ctx) {
        // Create the root LangCompilationUnit
        LangCompilationUnit langCompilationUnit = LangASTNodeFactory.createCompilationUnit(ctx);

        // Visit all statements in the file and add them to the LangCompilationUnit
        for (Python3Parser.StmtContext stmtCtx : ctx.stmt()) {
            LangASTNode child = mainBuilder.visit(stmtCtx);
            if (child != null) {
                if (child instanceof LangTypeDeclaration) {
                    langCompilationUnit.addType((LangTypeDeclaration) child);
                } else {
                    langCompilationUnit.addChild(child); // Only add non-types as children
                }
            }
        }
        System.out.println("LangCompilationUnit: " + langCompilationUnit.getChildren().toString());
        return langCompilationUnit;
    }

}
