package antlr.jdtmapper;

import antlr.ast.builder.python.PyASTBuilder;
import antlr.ast.node.LangASTNode;
import antlr.ast.node.unit.LangCompilationUnit;
import antlr.base.lang.python.Python3Lexer;
import antlr.base.lang.python.Python3Parser;
import com.github.gumtreediff.tree.TreeContext;
import org.antlr.v4.runtime.*;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.IOException;
import java.io.Reader;

import static antlr.base.factory.LangLexerFactory.getLexer;
import static antlr.base.factory.LangParserFactory.getParser;
import static antlr.jdtmapper.JdtASTMapperRegistry.getMapper;

/**
 * Mapper Factory class to get the JDT AST mapper
 * based on the name of the programming language or file extension
 */
public class LangJdtASTConverter {

    public static LangASTNode getCustomPythonAST(Reader r) throws IOException {
        // Create a new TreeContext to hold our tree
        TreeContext context = new TreeContext();

        // Parse the Python code
        CharStream input = CharStreams.fromReader(r);
        Python3Lexer lexer = new Python3Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Python3Parser parser = new Python3Parser(tokens);

        // Get the parse tree
        Python3Parser.File_inputContext parseTree = parser.file_input();

        // Build our custom AST
        PyASTBuilder astBuilder = new PyASTBuilder();
//        ParserRuleContext parseTree = LangJdtASTConverter.getParseTree("python", r);
        LangASTNode pythonAST = astBuilder.build(parseTree);

        return pythonAST;
    }

    public static CompilationUnit getJdtASTFromLangParseTree(String language, String code) {
        // Normalize the language identifier
        String lang = language.toLowerCase();

        // Get the appropriate lexer for the language
        CharStream input = CharStreams.fromString(code);
        Lexer lexer = getLexer(lang, input);

        // Create token stream
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Get the appropriate parser for the language
        Parser parser = getParser(lang, tokens);

        ParserRuleContext parseTree = getParseTree(lang, parser);

        LangASTNode langAST = getLangAST(lang, parseTree);

        return getCompatibleJdtAST(lang, (LangCompilationUnit) langAST);
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


    public static CompilationUnit getCompatibleJdtAST(String language, LangCompilationUnit langCompilationUnit){
        JdtASTMapper mapper = getMapper(language);

        AST ast = AST.newAST(AST.getJLSLatest(), false);

        return (CompilationUnit) mapper.map(langCompilationUnit, ast);

    }

}
