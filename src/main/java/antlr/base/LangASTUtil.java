package antlr.base;

import antlr.ast.builder.python.PyASTBuilder;
import antlr.ast.node.LangASTNode;
import antlr.base.lang.python.Python3Lexer;
import antlr.base.lang.python.Python3Parser;
import com.github.gumtreediff.tree.TreeContext;
import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.io.Reader;

/**
 * Mapper Factory class to get the JDT AST mapper
 * based on the name of the programming language or file extension
 */
public class LangASTUtil {

    public static LangASTNode getCustomPythonAST(Reader r) throws IOException {

        // Parse the Python code
        CharStream input = CharStreams.fromReader(r);
        Python3Lexer lexer = new Python3Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Python3Parser parser = new Python3Parser(tokens);

        // Get the parse tree
        Python3Parser.File_inputContext parseTree = parser.file_input();
        System.out.println(parseTree.toStringTree(parser));

        // Build our custom AST
        PyASTBuilder astBuilder = new PyASTBuilder();

        return astBuilder.build(parseTree);
    }

    public static ParserRuleContext getParseTree(String lang, Parser parser){
        ParserRuleContext parseTree;
        try {
            if ("python".equals(lang) || "py".equals(lang)) {
                parseTree = ((Python3Parser)parser).file_input();
            } else {
                // Handle other languages as they are added
                throw new UnsupportedOperationException("Parser rule not defined for language: " + lang);
            }
        } catch (ClassCastException e) {
            throw new RuntimeException("Parser type mismatch for language: " + lang, e);
        }
        return parseTree;
    }

    public static LangASTNode getLangAST(String lang, ParserRuleContext parseTree){
        LangASTNode langAST;
        if ("python".equals(lang) || "py".equals(lang)) {
            PyASTBuilder astBuilder = new PyASTBuilder();
            langAST = astBuilder.build((Python3Parser.File_inputContext) parseTree);
        } else {
            throw new UnsupportedOperationException("AST builder not defined for language: " + lang);
        }
        return langAST;
    }

}
