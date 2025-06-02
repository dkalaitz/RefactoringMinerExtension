package antlr.umladapter.processor;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.expression.*;
import antlr.ast.node.statement.LangBlock;
import antlr.ast.node.statement.LangExpressionStatement;
import antlr.ast.node.statement.LangReturnStatement;
import gr.uom.java.xmi.LocationInfo;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.decomposition.*;

import java.util.List;


public class UMLAdapterStatementProcessor {

    public static void processStatement(LangASTNode statement, CompositeStatementObject composite,
                                        String sourceFolder, String filePath, UMLOperation container) {

        // Process the statement itself first, not just its children
        if (statement instanceof LangReturnStatement returnStmt) {
            processReturnStatement(returnStmt, composite, sourceFolder, filePath, container);
        } else if (statement instanceof LangAssignment langAssignment) {
            processAssignment(langAssignment, composite, sourceFolder, filePath, container);
        } else if (statement instanceof LangMethodInvocation langMethodInvocation) {
            processMethodInvocation(langMethodInvocation, composite, sourceFolder, filePath, container);
        } else if (statement instanceof LangFieldAccess fieldAccess) {
            processFieldAccess(fieldAccess, composite, sourceFolder, filePath, container);
        } else if (statement instanceof LangBlock block) {
            processBlock(block, composite, sourceFolder, filePath, container);
        } else if (statement instanceof LangExpressionStatement expressionStatement) {
            processExpressionStatement(expressionStatement, composite, sourceFolder, filePath, container);
        }
        // TODO
        // Add handlers for other Python statement types:
        // - Try/except blocks
        // - With statements
        // - Break/continue
        // - etc.

        // If no specific handler, create a generic statement object
    }


    public static void processAssignment(LangAssignment assignment, CompositeStatementObject composite,
                                         String sourceFolder, String filePath, UMLOperation container) {
        // Create the assignment statement object
        StatementObject assignmentStatement = new StatementObject(
                assignment.getRootCompilationUnit(),
                sourceFolder,
                filePath,
                assignment,
                composite.getDepth() + 1,
                LocationInfo.CodeElementType.ASSIGNMENT,
                container
        );

        composite.addStatement(assignmentStatement);
    }



    public static void processFieldAccess(LangFieldAccess fieldAccess, CompositeStatementObject composite,
                                    String sourceFolder, String filePath, UMLOperation container) {
        // Track field access for detecting Move Field/Method refactorings
        StatementObject fieldAccessStatement = new StatementObject(
                fieldAccess.getRootCompilationUnit(),
                sourceFolder,
                filePath,
                fieldAccess,
                composite.getDepth() + 1,
                LocationInfo.CodeElementType.FIELD_ACCESS,
                container
        );
        composite.addStatement(fieldAccessStatement);
    }

    public static void processMethodInvocation(LangMethodInvocation methodInvocation, CompositeStatementObject composite,
                                               String sourceFolder, String filePath, UMLOperation container) {
        StatementObject methodInvocationStatement = new StatementObject(
                methodInvocation.getRootCompilationUnit(),
                sourceFolder,
                filePath,
                methodInvocation,
                composite.getDepth() + 1,
                LocationInfo.CodeElementType.METHOD_INVOCATION,
                container
        );
        composite.addStatement(methodInvocationStatement);
    }


    public static void processReturnStatement(LangReturnStatement returnStmt, CompositeStatementObject composite,
                                              String sourceFolder, String filePath, UMLOperation container){
        StatementObject returnStatement = new StatementObject(
                returnStmt.getRootCompilationUnit(),
                sourceFolder,
                filePath,
                returnStmt,
                composite.getDepth() + 1,
                LocationInfo.CodeElementType.RETURN_STATEMENT,
                container
        );
        composite.addStatement(returnStatement);
    }

    public static void processBlock(LangBlock block, CompositeStatementObject composite, String sourceFolder, String filePath, UMLOperation container) {
        // For blocks, create a composite statement and process its children
        CompositeStatementObject blockObject = new CompositeStatementObject(
                block.getRootCompilationUnit(),
                sourceFolder,
                filePath,
                block,
                composite.getDepth() + 1,
                LocationInfo.CodeElementType.BLOCK
        );
        composite.addStatement(blockObject);

        // Process each statement in the block
        for (LangASTNode childStatement : block.getStatements()) {
            processStatement(childStatement, blockObject, sourceFolder, filePath, container);
        }
    }

    public static void processExpressionStatement(LangExpressionStatement expressionStatement, CompositeStatementObject composite, String sourceFolder, String filePath, UMLOperation container) {
        // Check if the expression is a method invocation
        LangASTNode expression = expressionStatement.getExpression();

        if (expression instanceof LangAssignment assignment) {
            // ✅ CREATE ASSIGNMENT STATEMENT, NOT EXPRESSION STATEMENT
            StatementObject assignmentStatement = new StatementObject(
                    expressionStatement.getRootCompilationUnit(),
                    sourceFolder,
                    filePath,
                    expressionStatement, // Use the expression statement as the node
                    composite.getDepth() + 1,
                    LocationInfo.CodeElementType.ASSIGNMENT,  // ✅ CORRECT TYPE!
                    container
            );
            composite.addStatement(assignmentStatement);
        } else if (expression instanceof LangMethodInvocation methodInvocation) {
            // Create a statement object for the method invocation
            StatementObject methodInvocationStatement = new StatementObject(
                    expressionStatement.getRootCompilationUnit(),
                    sourceFolder,
                    filePath,
                    expressionStatement,
                    composite.getDepth() + 1,
                    LocationInfo.CodeElementType.METHOD_INVOCATION,
                    container
            );
            composite.addStatement(methodInvocationStatement);
        } else {
            // Handle other expression types
            StatementObject expressionStatementObject = new StatementObject(
                    expressionStatement.getRootCompilationUnit(),
                    sourceFolder,
                    filePath,
                    expressionStatement,
                    composite.getDepth() + 1,
                    LocationInfo.CodeElementType.EXPRESSION_STATEMENT,
                    container
            );
            composite.addStatement(expressionStatementObject);
        }

    }

}
