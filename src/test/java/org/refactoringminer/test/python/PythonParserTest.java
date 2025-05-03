package org.refactoringminer.test.python;

import antlr.base.lang.python.Python3Lexer;
import antlr.base.lang.python.Python3Parser;
import antlr.base.lang.python.PythonTreeListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;


@Isolated
public class PythonParserTest {

    private final static Logger logger = LoggerFactory.getLogger(PythonParserTest.class);

    @Test
    public void pythonParserTest() {
        // Specify the path to the Python file
        Path pythonFilePath = Path.of("C:/Users/popos/Desktop/pyfiles/pygame.py");

        try {

            long startTime = System.nanoTime();

            // Read the Python file into a CharStream
            CharStream charStream = CharStreams.fromPath(pythonFilePath);

            // Create a lexer and parser for the Python code
            Python3Lexer lexer = new Python3Lexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Python3Parser parser = new Python3Parser(tokens);

            // Start parsing at the root rule
            Python3Parser.File_inputContext tree = parser.file_input();

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;

            //JFrame frame = new JFrame("Antlr AST");
           // JPanel panel = new JPanel();
            /*TreeViewer viewer = new TreeViewer(Arrays.asList(
                    parser.getRuleNames()),tree);
            viewer.setScale(1.5); // Scale a little
            panel.add(viewer);
            frame.add(panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);*/

            // Print the parse tree for inspection
            logger.info("Parse tree: {}, Time taken to generate parse tree: {}", tree.toStringTree(parser), duration);

            // Use a listener to print each part of the tree
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(new PythonTreeListener(), tree);

            System.out.println("Parse tree: " + tree.toStringTree(parser));
            System.out.println("Time taken to generate parse tree: " + duration);


        } catch (IOException e) {
            logger.error("Failed to read the Python file", e);
        }
    }
}
