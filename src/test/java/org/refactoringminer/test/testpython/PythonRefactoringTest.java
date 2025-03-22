package org.refactoringminer.test.testpython;

import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLModelASTReader;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringMinerTimedOutException;

import java.util.*;

@Isolated
public class PythonRefactoringTest {

    @Test
    public void testRenameRefactoring() throws RefactoringMinerTimedOutException {
        // Test with sample Python code before and after refactoring
        String beforeRefactoring =
                """
                        def add(a, b):
                            return a + b
                        
                        def subtract(a, b):
                            return a - b
                        
                        def multiply(a, b):
                            return a * b
                        
                        def divide(a, b):
                            return a / b
                        
                        # Calculate result
                        result = add(10, 5)
                        print(result)""";

        // After refactoring - renamed method and extracted common code
        String afterRefactoring =
                // Rename add -> sum
                """
                        def sum(a, b):\s
                            return a + b
                        
                        def subtract(a, b):
                            return a - b
                        
                        def multiply(a, b):
                            return a * b
                        
                        def divide(a, b):
                            return a / b
                        
                        def calculate(a, b, operation):
                            if operation == 'add':
                                return sum(a, b)
                            elif operation == 'subtract':
                                return subtract(a, b)
                            elif operation == 'multiply':
                                return multiply(a, b)
                            elif operation == 'divide':
                                return divide(a, b)
                        
                        # Calculate result
                        result = calculate(10, 5, 'add')
                        print(result)""";


        // Set up before files
        Map<String, String> beforeFiles = new HashMap<>();
        beforeFiles.put("calculator.py", beforeRefactoring);

        // Set up after files
        Map<String, String> afterFiles = new HashMap<>();
        afterFiles.put("calculator.py", afterRefactoring);

        // Create UML models
        Set<String> repositoryDirectories = new HashSet<>();
        repositoryDirectories.add("/src");

        // Set astDiff to false for initial testing
        boolean astDiff = false;
        UMLModelASTReader beforeModelReader = new UMLModelASTReader(beforeFiles, repositoryDirectories, astDiff);
        UMLModel beforeModel = beforeModelReader.getUmlModel();

        UMLModelASTReader afterModelReader = new UMLModelASTReader(afterFiles, repositoryDirectories, astDiff);
        UMLModel afterModel = afterModelReader.getUmlModel();

        // Compare models to detect refactorings
        UMLModelDiff modelDiff = new UMLModelDiff(beforeModel, afterModel);
        List<Refactoring> refactorings = modelDiff.getRefactorings();

        // Print detected refactorings
        System.out.println("Detected " + refactorings.size() + " refactorings:");
        for (Refactoring refactoring : refactorings) {
            System.out.println(refactoring.toString());
        }
    }

}