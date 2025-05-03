package org.refactoringminer.test.python;

import antlr.jdtmapper.LangJdtASTConverter;
import org.eclipse.jdt.core.dom.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.refactoringminer.utils.LangASTUtil.readResourceFile;

@Isolated
public class PyJdtASTMapperTest {

    @Test
    public void testPythonCalculatorToJDT() {
        String pythonCode = "class Calculator:\n" +
                "    def add(self, x, y):\n" +
                "        x = x + y\n" +
                "        return x";

        // Convert Python code to JDT AST
        CompilationUnit jdtAST = LangJdtASTConverter.getJdtASTFromLangParseTree("python", pythonCode);
        assertNotNull(jdtAST, "The JDT AST should not be null");

        // Verify the structure of the resulting JDT AST
        List<?> types = jdtAST.types();
        assertEquals(1, types.size(), "Should have one type declaration");
        TypeDeclaration calculatorClass = (TypeDeclaration) types.get(0);
        assertEquals("Calculator", calculatorClass.getName().getIdentifier(), "Class name should be 'Calculator'");

        // Check method
        MethodDeclaration[] methods = calculatorClass.getMethods();
        assertEquals(1, methods.length, "There should be one method");
        MethodDeclaration addMethod = methods[0];
        assertEquals("add", addMethod.getName().getIdentifier(), "Method name should be 'add'");

        // Check parameters
        List<?> parameters = addMethod.parameters();
        assertEquals(3, parameters.size(), "Method should have 3 parameters");
        assertEquals("self", ((SingleVariableDeclaration)parameters.get(0)).getName().getIdentifier());
        assertEquals("x", ((SingleVariableDeclaration)parameters.get(1)).getName().getIdentifier());
        assertEquals("y", ((SingleVariableDeclaration)parameters.get(2)).getName().getIdentifier());

        // Check method body structure - this is where we need to verify the expected structure
        Block body = addMethod.getBody();
        List<Statement> statements = body.statements();

        // Based on the custom JDT representation, we expect two blocks:
        // One with the assignment statement and one with the return statement
        // OR we might have two direct statements depending on the mapping implementation

        // Print statements for debugging
        System.out.println("Method body has " + statements.size() + " statements");
        for (Statement stmt : statements) {
            System.out.println("Statement type: " + stmt.getClass().getSimpleName());
        }

        // Check if we have nested blocks or direct statements
        if (statements.size() == 2 && statements.get(0) instanceof Block) {
            // Case 1: We have nested blocks as per your custom JDT representation
            Block assignBlock = (Block) statements.get(0);
            Block returnBlock = (Block) statements.get(1);

            // Verify assignment block
            List<Statement> assignStatements = assignBlock.statements();
            assertEquals(1, assignStatements.size(), "Assignment block should have one statement");
            assertTrue(assignStatements.get(0) instanceof ExpressionStatement,
                    "First statement should be an expression statement");

            ExpressionStatement exprStmt = (ExpressionStatement) assignStatements.get(0);
            assertTrue(exprStmt.getExpression() instanceof Assignment,
                    "Expression should be an assignment");

            Assignment assignment = (Assignment) exprStmt.getExpression();
            assertEquals(Assignment.Operator.ASSIGN, assignment.getOperator(), "Operator should be =");
            assertTrue(assignment.getLeftHandSide() instanceof SimpleName,
                    "Left side should be a simple name");
            assertEquals("x", ((SimpleName)assignment.getLeftHandSide()).getIdentifier(),
                    "Left side should be x");

            // Check right side (x + y)
            assertTrue(assignment.getRightHandSide() instanceof InfixExpression,
                    "Right side should be an infix expression");
            InfixExpression infixExpr = (InfixExpression) assignment.getRightHandSide();
            assertEquals(InfixExpression.Operator.PLUS, infixExpr.getOperator(),
                    "Infix operator should be +");
            assertEquals("x", ((SimpleName)infixExpr.getLeftOperand()).getIdentifier(),
                    "Left operand should be x");
            assertEquals("y", ((SimpleName)infixExpr.getRightOperand()).getIdentifier(),
                    "Right operand should be y");

            // Verify return block
            List<Statement> returnStatements = returnBlock.statements();
            assertEquals(1, returnStatements.size(), "Return block should have one statement");
            assertTrue(returnStatements.get(0) instanceof ReturnStatement,
                    "Second statement should be a return statement");

            ReturnStatement returnStmt = (ReturnStatement) returnStatements.get(0);
            assertTrue(returnStmt.getExpression() instanceof SimpleName,
                    "Return expression should be a simple name");
            assertEquals("x", ((SimpleName)returnStmt.getExpression()).getIdentifier(),
                    "Return value should be x");
        } else {
            // Case 2: We have direct statements (no nesting)
            // Likely structure: [ExpressionStatement(Assignment), ReturnStatement]
            assertEquals(2, statements.size(), "Method body should have 2 statements");

            // First statement should be the assignment x = x + y
            assertTrue(statements.get(0) instanceof ExpressionStatement,
                    "First statement should be an expression statement");
            ExpressionStatement exprStmt = (ExpressionStatement) statements.get(0);
            assertTrue(exprStmt.getExpression() instanceof Assignment,
                    "Expression should be an assignment");

            Assignment assignment = (Assignment) exprStmt.getExpression();
            assertEquals(Assignment.Operator.ASSIGN, assignment.getOperator());
            assertEquals("x", ((SimpleName)assignment.getLeftHandSide()).getIdentifier());

            // Check right side (x + y)
            assertTrue(assignment.getRightHandSide() instanceof InfixExpression);
            InfixExpression infixExpr = (InfixExpression) assignment.getRightHandSide();
            assertEquals(InfixExpression.Operator.PLUS, infixExpr.getOperator());
            assertEquals("x", ((SimpleName)infixExpr.getLeftOperand()).getIdentifier());
            assertEquals("y", ((SimpleName)infixExpr.getRightOperand()).getIdentifier());

            // Second statement should be return x
            assertTrue(statements.get(1) instanceof ReturnStatement,
                    "Second statement should be a return statement");
            ReturnStatement returnStmt = (ReturnStatement) statements.get(1);
            assertTrue(returnStmt.getExpression() instanceof SimpleName);
            assertEquals("x", ((SimpleName)returnStmt.getExpression()).getIdentifier());
        }

        System.out.println("Successfully verified JDT AST structure");
    }

    @Test
    public void testPythonSourcePositions() throws IOException {
        String pythonCode = readResourceFile("python-samples/before/calculator.py");
//        String pythonCode = "class Calculator:\n" +
//                "    def add(self, x, y):\n" +
//                "        x = x + y\n" +
//                "        return x";

        // Print the code with position markers
        System.out.println("Source code with position markers:");
        for (int i = 0; i < pythonCode.length(); i += 10) {
            System.out.println(String.format("%3d: %s", i,
                    pythonCode.substring(i, Math.min(i + 10, pythonCode.length()))));
        }

        // Convert and get the AST
        CompilationUnit jdtAST = LangJdtASTConverter.getJdtASTFromLangParseTree("python", pythonCode);

        jdtAST.accept(new ASTVisitor() {
            @Override
            public void preVisit(ASTNode node) {
                // Log every node type we encounter
                System.out.println("Found node: " + node.getClass().getSimpleName());

                // For nodes with source information, log the details
                if (node.getStartPosition() >= 0 && node.getLength() > 0) {
                    System.out.println("  With position: " + node.getStartPosition() +
                            ", length: " + node.getLength() +
                            ", content: '" + pythonCode.substring(
                            node.getStartPosition(),
                            Math.min(node.getStartPosition() + node.getLength(), pythonCode.length())) +
                            "'");
                } else {
                    System.out.println("  Without valid source position");
                }
            }
        });

    }

}

