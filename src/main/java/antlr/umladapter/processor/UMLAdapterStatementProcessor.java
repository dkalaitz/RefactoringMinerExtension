package antlr.umladapter.processor;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.expression.*;
import antlr.ast.node.statement.*;
import antlr.ast.visitor.LangVisitor;
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
        } else if (statement instanceof LangIfStatement ifStatement) {
            processIfStatement(ifStatement, composite, sourceFolder, filePath, container);
        } else if (statement instanceof LangWhileStatement whileStatement){
            processWhileStatement(whileStatement, composite, sourceFolder, filePath, container);
        } else if (statement instanceof LangForStatement forStatement){
            processForStatement(forStatement, composite, sourceFolder, filePath, container);
        } else if (statement instanceof LangSwitchStatement switchStatement){
            processSwitchStatement(switchStatement, composite, sourceFolder, filePath, container);
        } else if (statement instanceof LangTryStatement tryStatement){
            processTryStatement(tryStatement, composite, sourceFolder, filePath, container);
        } else if (statement instanceof LangBreakStatement breakStatement){
            processBreakStatement(breakStatement, composite, sourceFolder, filePath, container);
        } else if (statement instanceof LangContinueStatement continueStatement){
            processContinueStatement(continueStatement, composite, sourceFolder, filePath, container);
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
            StatementObject assignmentStatement = new StatementObject(
                    expressionStatement.getRootCompilationUnit(),
                    sourceFolder,
                    filePath,
                    expressionStatement, // Use the expression statement as the node
                    composite.getDepth() + 1,
                    LocationInfo.CodeElementType.ASSIGNMENT,
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

    public static void processIfStatement(LangIfStatement ifStatement, CompositeStatementObject composite,
                                          String sourceFolder, String filePath, UMLOperation container) {
        // Process the condition expression
        if (ifStatement.getCondition() != null) {
            ifStatement.getCondition().accept(new LangVisitor(
                    ifStatement.getRootCompilationUnit(), sourceFolder, filePath, container));
        }


        // Process the else block if it exists
        if (ifStatement.getElseBody() != null) {
            processStatement(ifStatement.getElseBody(), composite, sourceFolder, filePath, container);
        }
    }

    public static void processWhileStatement(LangWhileStatement whileStatement, CompositeStatementObject composite,
                                             String sourceFolder, String filePath, UMLOperation container) {
        // Process the condition expression
        if (whileStatement.getCondition() != null) {
            whileStatement.getCondition().accept(new LangVisitor(
                    whileStatement.getRootCompilationUnit(), sourceFolder, filePath, container));
        }

        // Process the loop body
        if (whileStatement.getBody() != null) {
            processStatement(whileStatement.getBody(), composite, sourceFolder, filePath, container);
        }
    }

    public static void processForStatement(LangForStatement forStatement, CompositeStatementObject composite,
                                           String sourceFolder, String filePath, UMLOperation container) {
        // Create a CompositeStatementObject for the for statement
        CompositeStatementObject forStatementObject = new CompositeStatementObject(
                forStatement.getRootCompilationUnit(),
                sourceFolder,
                filePath,
                forStatement,
                composite.getDepth() + 1,
                LocationInfo.CodeElementType.FOR_STATEMENT
        );

        // Process the initializers (loop variables)
        if (forStatement.getInitializers() != null) {
            for (LangSingleVariableDeclaration initializer : forStatement.getInitializers()) {
                initializer.accept(new LangVisitor(
                        forStatement.getRootCompilationUnit(), sourceFolder, filePath, container));
            }
        }

        // Process the condition expression (for traditional for loops)
        if (forStatement.getCondition() != null) {
            forStatement.getCondition().accept(new LangVisitor(
                    forStatement.getRootCompilationUnit(), sourceFolder, filePath, container));
        }

        // Process the update expressions
        if (forStatement.getUpdates() != null) {
            for (LangASTNode update : forStatement.getUpdates()) {
                update.accept(new LangVisitor(
                        forStatement.getRootCompilationUnit(), sourceFolder, filePath, container));
            }
        }

        // Process the loop body
        if (forStatement.getBody() != null) {
            processStatement(forStatement.getBody(), forStatementObject, sourceFolder, filePath, container);
        }

        // Process the else body (Python for-else construct)
        if (forStatement.getElseBody() != null) {
            processStatement(forStatement.getElseBody(), forStatementObject, sourceFolder, filePath, container);
        }

        // Add the for statement to the parent composite
        composite.addStatement(forStatementObject);
    }


    public static void processSwitchStatement(LangSwitchStatement switchStatement, CompositeStatementObject composite,
                                              String sourceFolder, String filePath, UMLOperation container) {
        // Create a CompositeStatementObject for the switch statement
        CompositeStatementObject switchStatementObject = new CompositeStatementObject(
                switchStatement.getRootCompilationUnit(),
                sourceFolder,
                filePath,
                switchStatement,
                composite.getDepth() + 1,
                LocationInfo.CodeElementType.SWITCH_STATEMENT
        );

        // Process the switch expression
        if (switchStatement.getExpression() != null) {
            switchStatement.getExpression().accept(new LangVisitor(
                    switchStatement.getRootCompilationUnit(), sourceFolder, filePath, container));
        }

        // Process each case statement
        if (switchStatement.getCases() != null) {
            for (LangASTNode caseStmt : switchStatement.getCases()) {
                processStatement(caseStmt, switchStatementObject, sourceFolder, filePath, container);
            }
        }

        // Add the switch statement to the parent composite
        composite.addStatement(switchStatementObject);
    }


    public static void processTryStatement(LangTryStatement tryStatement, CompositeStatementObject composite,
                                           String sourceFolder, String filePath, UMLOperation container) {
        // Create a TryStatementObject (specialized composite for try statements)
        TryStatementObject tryStatementObject = new TryStatementObject(
                tryStatement.getRootCompilationUnit(),
                sourceFolder,
                filePath,
                tryStatement,
                composite.getDepth() + 1
        );

        // Process the try block
        if (tryStatement.getBody() != null) {
            processStatement(tryStatement.getBody(), tryStatementObject, sourceFolder, filePath, container);
        }

        // Process catch clauses
        if (tryStatement.getCatchClauses() != null) {
            for (LangCatchClause catchClause : tryStatement.getCatchClauses()) {
                CompositeStatementObject catchStatementObject = new CompositeStatementObject(
                        catchClause.getRootCompilationUnit(),
                        sourceFolder,
                        filePath,
                        catchClause,
                        tryStatementObject.getDepth() + 1,
                        LocationInfo.CodeElementType.CATCH_CLAUSE
                );

                if (catchClause.getBody() != null) {
                    processStatement(catchClause.getBody(), catchStatementObject, sourceFolder, filePath, container);
                }

                tryStatementObject.addCatchClause(catchStatementObject);
            }
        }

        // Process finally block
        if (tryStatement.getFinallyBlock() != null) {
            CompositeStatementObject finallyStatementObject = new CompositeStatementObject(
                    tryStatement.getFinallyBlock().getRootCompilationUnit(),
                    sourceFolder,
                    filePath,
                    tryStatement.getFinallyBlock(),
                    tryStatementObject.getDepth() + 1,
                    LocationInfo.CodeElementType.FINALLY_BLOCK
            );

            processStatement(tryStatement.getFinallyBlock(), finallyStatementObject, sourceFolder, filePath, container);
            tryStatementObject.setFinallyClause(finallyStatementObject);
        }

        // Add the try statement to the parent composite
        composite.addStatement(tryStatementObject);
    }

    public static void processBreakStatement(LangBreakStatement breakStatement, CompositeStatementObject composite,
                                             String sourceFolder, String filePath, UMLOperation container) {
        // Create a simple statement object for break
        StatementObject breakStmt = new StatementObject(
                breakStatement.getRootCompilationUnit(),
                sourceFolder,
                filePath,
                breakStatement,
                0, // depth
                LocationInfo.CodeElementType.BREAK_STATEMENT,
                container
        );
        composite.addStatement(breakStmt);
    }

    public static void processContinueStatement(LangContinueStatement continueStatement, CompositeStatementObject composite,
                                                String sourceFolder, String filePath, UMLOperation container) {
        // Create a simple statement object for continue
        StatementObject continueStmt = new StatementObject(
                continueStatement.getRootCompilationUnit(),
                sourceFolder,
                filePath,
                continueStatement,
                0, // depth
                LocationInfo.CodeElementType.CONTINUE_STATEMENT,
                container
        );
        composite.addStatement(continueStmt);
    }

}
