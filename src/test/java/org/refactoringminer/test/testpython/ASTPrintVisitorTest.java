package org.refactoringminer.test.testpython;

import antlr.python.Python3Lexer;
import antlr.python.Python3Parser;
import antlr.python.ast.ASTPrintVisitor;
import antlr.python.ast.PythonASTBuilder;
import antlr.python.ast.elements.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Isolated
public class ASTPrintVisitorTest {

    @Test
    public void testASTVisitor() throws IOException {
        // Example Python code
        String code = "class Calculator:\n" +
                "    def add(self, x, y):\n" +
                "        return x + y";

        code = "if x > 10:\n" +
                "    print(\"x is greater than 10\")\n";
        Path pythonFilePath = Path.of("C:/Users/popos/Desktop/pyfiles/sample.py");


        // Parse the code using ANTLR
        //Python3Lexer lexer = new Python3Lexer(CharStreams.fromString(code));

        CharStream charStream = CharStreams.fromPath(pythonFilePath);
        Python3Lexer lexer = new Python3Lexer(charStream);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Python3Parser parser = new Python3Parser(tokens);
        Python3Parser.File_inputContext parseTree = parser.file_input();  // Generate the parse tree

        // Build the AST using the PythonASTBuilder
        PythonASTBuilder astBuilder = new PythonASTBuilder();
        ASTNode ast = astBuilder.visit(parseTree);  // Build the AST

        // Debug: Print the AST nodes during construction
        System.out.println("AST Construction Complete");

        // Print the AST using the ASTPrintVisitor
        ASTPrintVisitor printVisitor = new ASTPrintVisitor();
        ast.accept(printVisitor);  // This should print the AST in a readable format
    }
}
