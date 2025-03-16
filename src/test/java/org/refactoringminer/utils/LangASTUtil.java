package org.refactoringminer.utils;


import antlr.ast.builder.python.PyASTBuilder;
import antlr.ast.node.LangASTNode;
import antlr.ast.visitor.LangASTPrinter;
import antlr.base.python.Python3Lexer;
import antlr.base.python.Python3Parser;
import antlr.jdtmapper.JdtASTMapper;
import antlr.jdtmapper.JdtASTMapperFactory;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;

import java.io.IOException;
import java.nio.file.Path;

public class LangASTUtil {

    public static void printAST(String code) {
        // Parse the code using ANTLR

        Python3Lexer lexer = new Python3Lexer(CharStreams.fromString(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Python3Parser parser = new Python3Parser(tokens);
        Python3Parser.File_inputContext parseTree = parser.file_input();  // Generate the parse tree

        // Build the AST using the PythonASTBuilder
        PyASTBuilder astBuilder = new PyASTBuilder();
        LangASTNode ast = astBuilder.build(parseTree);  // Build the AST

        // Debug: Print the parse tree for reference
        System.out.println("Parse Tree:");
        System.out.println(parseTree.toStringTree(parser));

        // Print the AST using the ASTPrinter
        System.out.println("\nAST Structure:");
        LangASTPrinter printer = new LangASTPrinter();
        ast.accept(printer);
    }

    public static void printAST(Path pythonFilePath) throws IOException {
        // Parse the code using ANTLR

        CharStream charStream = CharStreams.fromPath(pythonFilePath);
        Python3Lexer lexer = new Python3Lexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Python3Parser parser = new Python3Parser(tokens);
        Python3Parser.File_inputContext parseTree = parser.file_input();  // Generate the parse tree

        // Build the AST using the PythonASTBuilder
        PyASTBuilder astBuilder = new PyASTBuilder();
        LangASTNode ast = astBuilder.build(parseTree);  // Build the AST

        // Debug: Print the parse tree for reference
        System.out.println("Parse Tree:");
        System.out.println(parseTree.toStringTree(parser));

        // Print the AST using the ASTPrinter
        System.out.println("\nAST Structure:");
        LangASTPrinter printer = new LangASTPrinter();
        ast.accept(printer);
    }

    public static ASTNode mapToJdt(String code) {

        JdtASTMapper mapper = JdtASTMapperFactory.getMapper("python");
        LangASTNode pyAST = parsePythonCodeToAST(code);

        // Create AST
        AST jdtAst = AST.newAST(AST.JLS17);


        // Map to JDT
        return mapper.map(pyAST, jdtAst);
    }


    public static LangASTNode parsePythonCodeToAST(String code) {
        // Use the Python lexer and parser to parse the code
        Python3Lexer lexer = new Python3Lexer(CharStreams.fromString(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Python3Parser parser = new antlr.base.python.Python3Parser(tokens);
        Python3Parser.File_inputContext fileInputContext = parser.file_input();

        // Build our AST using the PyASTBuilder
        PyASTBuilder pyASTBuilder = new PyASTBuilder();
        return pyASTBuilder.build(fileInputContext);
    }



}
