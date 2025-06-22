package antlr.base;

import antlr.ast.builder.python.PyASTBuilder;
import antlr.ast.node.LangASTNode;
import antlr.base.lang.python.Python3Lexer;
import antlr.base.lang.python.Python3Parser;
import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.io.Reader;

/**
 * Utility class to create Language AST parsers
 * based on file extensions and supported programming languages
 */
public class LangASTUtil {


    public static LangASTNode getLangAST(String fileName, Reader reader) throws IOException {

        LangSupportedEnum language = LangSupportedEnum.fromFileName(fileName);

        if (language == null) {
            throw new UnsupportedOperationException("Language not supported for file: " + fileName);
        }

        switch (language) {
            case PYTHON:
                return getCustomPythonAST(reader);
            default:
                throw new UnsupportedOperationException("Parser not implemented for language: " + language);
        }

    }

    public static LangASTNode getCustomPythonAST(Reader r) throws IOException {

        // Parse the Python code
        CharStream input = CharStreams.fromReader(r);
        Python3Lexer lexer = new Python3Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Python3Parser parser = new Python3Parser(tokens);

        // Get the parse tree
        Python3Parser.File_inputContext parseTree = parser.file_input();

        // Build custom AST
        PyASTBuilder astBuilder = new PyASTBuilder();

        return astBuilder.build(parseTree);
    }


}
