package org.refactoringminer.test.testpython;

import antlr.base.python.Python3Lexer;
import antlr.base.python.Python3Parser;
import antlr.base.python.PythonTreeListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Isolated
public class PythonParserParallelTest {

    private final static Logger logger = LoggerFactory.getLogger(PythonParserParallelTest.class);

    @Test
    public void pythonParserParallelTest() {
        // Specify the directory where Python files are stored
        Path dirPath = Path.of("C:/Users/popos/Desktop/pyfiles");
        int cores = 1; //Runtime.getRuntime().availableProcessors();
        long totalStartTime = System.nanoTime(); // Start time for total execution


        // Get a list of all Python files in the directory
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.py")) {

            // Create a fixed thread pool
            ExecutorService executorService = Executors.newFixedThreadPool(cores);
            List<Future<Long>> futures = new ArrayList<>();

            // Submit parsing tasks for each Python file in the directory
            for (Path filePath : stream) {
                futures.add(executorService.submit(() -> parsePythonFile(filePath)));
            }
            long totalDuration = 0;  // Variable to accumulate total time

            // Wait for all tasks to complete
            for (Future<Long> future : futures) {
                try {
                    totalDuration += future.get(); // This will block until the task completes
                } catch (ExecutionException e) {
                    System.out.println("Error during file parsing: " + e.getMessage());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();  // Reset the interrupt status
                    System.out.println("Thread was interrupted" + e);
                }
            }

            // Shut down the executor service
            executorService.shutdown();

            long totalEndTime = System.nanoTime();  // End time for total execution
            long totalElapsedTime = (totalEndTime - totalStartTime) / 1_000_000;

            // Print the total parsing time and total execution time
            System.out.println("Total time to generate parse trees: " + totalDuration);
            System.out.println("Total execution time (including thread management): " + totalElapsedTime);
            System.out.println("Number of Cores: " + cores);
        } catch (IOException e) {
            logger.error("Failed to process Python files", e);
        }
    }

    private long parsePythonFile(Path pythonFilePath) {
        long duration = 0;
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
            duration = (endTime - startTime) / 1_000_000;

            // Print the parse tree for inspection (if desired)
            logger.info("Parse tree for file {}: {}, Time taken to generate parse tree: {} ms", pythonFilePath, tree.toStringTree(parser), duration);

            // Use a listener to print each part of the tree (optional)
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(new PythonTreeListener(), tree);

            // Print for debugging
            System.out.println("Parse tree for file: " + pythonFilePath);
            System.out.println("Parse tree: " + tree.toStringTree(parser));
            System.out.println("Time taken to generate parse tree: " + duration);

        } catch (IOException e) {
            logger.error("Failed to read the Python file " + pythonFilePath, e);
        }
        return duration;
    }
}
