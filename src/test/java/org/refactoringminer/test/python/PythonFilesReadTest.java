package org.refactoringminer.test.python;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Isolated
public class PythonFilesReadTest {

    @Test
    public void testPythonFilesReadDirectory() throws IOException {
        // Specify the directory path where the Python files are located
        Path pydir = Path.of("C:\\Users\\popos\\Desktop\\pyfiles");

        // Create a list to store the Python files
        List<File> pyFiles = new ArrayList<>();

        // Walk through the directory and filter for .py files
        Files.walk(pydir)
                .filter(path -> path.toString().endsWith(".py")) // Filter .py files
                .forEach(path -> pyFiles.add(path.toFile())); // Add files to the list

        // Verify that the list is not empty (i.e., at least one Python file exists in the directory)
        assertFalse(pyFiles.isEmpty(), "No Python files found in the directory.");

        // Optionally, print the Python files (for debugging purposes)
        pyFiles.forEach(file -> System.out.println("Found Python file: " + file.getName()));
    }
}
