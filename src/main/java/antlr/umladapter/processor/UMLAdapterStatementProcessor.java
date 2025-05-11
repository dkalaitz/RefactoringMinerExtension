package antlr.umladapter.processor;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.expression.LangAssignment;
import antlr.ast.node.expression.LangFieldAccess;
import antlr.ast.node.expression.LangInfixExpression;
import antlr.ast.node.expression.LangMethodInvocation;
import antlr.ast.node.statement.LangReturnStatement;
import gr.uom.java.xmi.LocationInfo;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.decomposition.CompositeStatementObject;
import gr.uom.java.xmi.decomposition.OperationInvocation;
import gr.uom.java.xmi.decomposition.StatementObject;

import java.util.ArrayList;
import java.util.List;

public class UMLAdapterStatementProcessor {

    public static void processAssignment(LangAssignment assignment, CompositeStatementObject composite,
                                         String sourceFolder, String filePath, UMLOperation container) {
        System.out.println("Assignment: " + assignment.toString());
        // Create the assignment statement object
        StatementObject assignmentStatement = new StatementObject(
                assignment.getRootCompilationUnit(),
                sourceFolder,
                filePath,
                assignment,
                composite.getDepth() + 1,
                LocationInfo.CodeElementType.ASSIGNMENT,
                container,
                null
        );

        // You should ideally create an ExpressionObject subtree for the right side,
        // but do NOT also add it as a statement in the composite.

        // Optionally, if your StatementObject supports it:
        // assignmentStatement.setRightHandSideExpression(...process recursively...);

        // Only add the assignment statement ONCE.
        composite.addStatement(assignmentStatement);
    }

    public static void processInfixExpression(LangInfixExpression infixExpr, CompositeStatementObject composite,
                                              String sourceFolder, String filePath, UMLOperation container) {
        StatementObject infixStatement = new StatementObject(
                infixExpr.getRootCompilationUnit(),
                sourceFolder,
                filePath,
                infixExpr,
                composite.getDepth() + 1,
                LocationInfo.CodeElementType.EXPRESSION_STATEMENT,
                container,
                null
        );

        composite.addStatement(infixStatement);
    }

//    public static void processMethodInvocation(LangMethodInvocation invocation, CompositeStatementObject composite,
//                                               String sourceFolder, String filePath, UMLOperation container) {
//        // Existing code...
//        String methodName = invocation.extractMethodName();
//
//        // Build OperationInvocation for the call
//        OperationInvocation operationInvocation = new OperationInvocation(
//                invocation.getRootCompilationUnit(),
//                sourceFolder,
//                filePath,
//                invocation,
//                LocationInfo.CodeElementType.METHOD_INVOCATION,
//                container
//        );
//        container.addInvocation(operationInvocation); // <-- ADD THIS LINE
//
//        // Existing statement creation...
//        StatementObject invocationStatement = new StatementObject(
//                invocation.getRootCompilationUnit(),
//                sourceFolder,
//                filePath,
//                invocation,
//                composite.getDepth() + 1,
//                LocationInfo.CodeElementType.EXPRESSION_STATEMENT,
//                container,
//                null
//        );
//        composite.addStatement(invocationStatement);
//    }


//    public static void processMethodInvocation(LangMethodInvocation invocation, CompositeStatementObject composite,
//                                         String sourceFolder, String filePath, UMLOperation container) {
//        // Extract method name from expression
//        String methodName = invocation.extractMethodName();
//        System.out.println("Method name: " + methodName);
//
//        // Get argument expressions
//        List<String> arguments = new ArrayList<>();
//        for (LangASTNode arg : invocation.getArguments()) {
//            System.out.println("Argument: " + arg.toString());
//            arguments.add(arg.toString());
//        }
//
//        // Determine if this is an instance method or a static/function call
//        boolean isInstanceMethod = isInstanceMethodCall(invocation);
//
//        // Create a representation of the method call
//        // This helps with detecting Extract/Inline Method refactorings
//        StatementObject invocationStatement = new StatementObject(
//                invocation.getRootCompilationUnit(),
//                sourceFolder,
//                filePath,
//                invocation,
//                composite.getDepth() + 1,
//                LocationInfo.CodeElementType.EXPRESSION_STATEMENT,
//                container,
//                null
//        );
//
//        // Add to the composite statement
//        composite.addStatement(invocationStatement);
//
//        // Track method invocation for later analysis (refactoring detection, etc.)
////        OperationInvocation abstractCall = new OperationInvocation(
////                invocation.getRootCompilationUnit(),
////                sourceFolder,
////                filePath,
////                invocation.getExpression(),
////                LocationInfo.CodeElementType.METHOD_INVOCATION,
////                container
////        );
//
//        // Add to method invocations list if you're tracking them
//        //  container.add(abstractCall);
//    }

    private static boolean isInstanceMethodCall(LangMethodInvocation invocation) {
        // For Python, check if the expression is a field access
        if (invocation.getExpression() instanceof LangFieldAccess) {
            LangFieldAccess fieldAccess = (LangFieldAccess) invocation.getExpression();
            // Check if it's not a call like ClassName.method()
            // In Python, an instance method is typically called via object.method()
            return true;
        }
        return false; // Assume it's a function call or static method call
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
                container,
                null
        );
        composite.addStatement(fieldAccessStatement);
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
                container,
                null  // No Java file content for Python
        );
        composite.addStatement(returnStatement);
    }
}
