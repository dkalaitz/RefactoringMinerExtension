package org.refactoringminer.test.testpython;

import antlr.python.base.Python3Lexer;
import antlr.python.base.Python3Parser;
import antlr.python.ast.visitor.ASTPrinter;
import antlr.python.ast.builder.PythonASTBuilder;
import antlr.python.node.ASTNode;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Isolated
public class ASTPrinterTest {

    @Test
    public void testASTVisitor() throws IOException {
        // Example Python code
        String code = "class Calculator:\n" +
                "    def add(self, x, y):\n" +
                "        return x + y";

       /* code = "if x==10:\n" +
                "    y = 20\n";*/
      /*  code = "if x==10:\n" +
                "    print(\"x is equal to 10\")\n" +
                "elif x>10:\n" +
                "   print(\"x is greater than 10\")\n" +
                "elif x<10:\n" +
                "   print(\"x is less than 10\")";;*/
        Path pythonFilePath = Path.of("C:/Users/popos/Desktop/pyfiles/sample.py");


        // Parse the code using ANTLR

        CharStream charStream = CharStreams.fromPath(pythonFilePath);
        //Python3Lexer lexer = new Python3Lexer(charStream);
        Python3Lexer lexer = new Python3Lexer(CharStreams.fromString(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Python3Parser parser = new Python3Parser(tokens);
        Python3Parser.File_inputContext parseTree = parser.file_input();  // Generate the parse tree

        // Build the AST using the PythonASTBuilder
        PythonASTBuilder astBuilder = new PythonASTBuilder();
        ASTNode ast = astBuilder.visit(parseTree);  // Build the AST

        // Debug: Print the parse tree for reference
        System.out.println("Parse Tree:");
        System.out.println(parseTree.toStringTree(parser));

        // Print the AST using the ASTPrinter
        System.out.println("\nAST Structure:");
        ASTPrinter printer = new ASTPrinter();
        ast.accept(printer); // Traverse the AST and print it
    }

  /*  @Test
    public void testVisitTest() {
        String code = "x > 10";
        Python3Lexer lexer = new Python3Lexer(CharStreams.fromString(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Python3Parser parser = new Python3Parser(tokens);

        Python3Parser.TestContext testCtx = parser.test();
        PythonASTBuilder astBuilder = new PythonASTBuilder();

        ASTNode conditionNode = astBuilder.visitTest(testCtx);

        assertNotNull(conditionNode, "ConditionNode should not be null");
        System.out.println("ConditionNode: " + conditionNode);
    }*/

}
