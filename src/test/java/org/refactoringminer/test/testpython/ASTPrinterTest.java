package org.refactoringminer.test.testpython;

import antlr.base.python.Python3Lexer;
import antlr.base.python.Python3Parser;
import antlr.ast.visitor.ASTPrinter;
import antlr.ast.builder.python.PythonASTBuilder;
import antlr.ast.node.ASTNode;
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
    public void testASTVisitor() {
        // Example Python code
        String code = "class Calculator:\n" +
                "    def add(self, x, y):\n" +
                "        x = x + y\n" +
                "        return x";
        printAST(code);
    }

    @Test
    public void testASTVisitor_IfStatement() {
        // Example Python code with if statement
        String code = "class Calculator:\n" +
                "    def add(self, x, y):\n" +
                "        if x > y:\n" +
                "            return x\n" +
                "        else:\n" +
                "            return y";

        printAST(code);
    }

    @Test
    public void testASTVisitor_ForLoop() {
        // Example Python code with for loop
        String code = "class ListProcessor:\n" +
                "    def process_numbers(self, numbers):\n" +
                "        sum = 0\n" +
                "        for num in numbers:\n" +
                "            sum = sum + num\n" +
                "        return sum";

        printAST(code);
    }

    @Test
    public void testASTVisitor_WhileLoop() {
        // Example Python code with for while
        String code = "class Counter:\n" +
                "    def count_up(self, limit):\n" +
                "        i = 0\n" +
                "        while i < limit:\n" +
                "            i = i + 1\n" +
                "            print(i)\n" +
                "        return i";

        printAST(code);
    }

    private void printAST(String code) {
        // Parse the code using ANTLR

        Python3Lexer lexer = new Python3Lexer(CharStreams.fromString(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Python3Parser parser = new Python3Parser(tokens);
        Python3Parser.File_inputContext parseTree = parser.file_input();  // Generate the parse tree

        // Build the AST using the PythonASTBuilder
        PythonASTBuilder astBuilder = new PythonASTBuilder();
        ASTNode ast = astBuilder.build(parseTree);  // Build the AST

        // Debug: Print the parse tree for reference
        System.out.println("Parse Tree:");
        System.out.println(parseTree.toStringTree(parser));

        // Print the AST using the ASTPrinter
        System.out.println("\nAST Structure:");
        ASTPrinter printer = new ASTPrinter();
        ast.accept(printer);
    }

    private void printAST(Path pythonFilePath) throws IOException {
        // Parse the code using ANTLR

        CharStream charStream = CharStreams.fromPath(pythonFilePath);
        Python3Lexer lexer = new Python3Lexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Python3Parser parser = new Python3Parser(tokens);
        Python3Parser.File_inputContext parseTree = parser.file_input();  // Generate the parse tree

        // Build the AST using the PythonASTBuilder
        PythonASTBuilder astBuilder = new PythonASTBuilder();
        ASTNode ast = astBuilder.build(parseTree);  // Build the AST

        // Debug: Print the parse tree for reference
        System.out.println("Parse Tree:");
        System.out.println(parseTree.toStringTree(parser));

        // Print the AST using the ASTPrinter
        System.out.println("\nAST Structure:");
        ASTPrinter printer = new ASTPrinter();
        ast.accept(printer);
    }
}
