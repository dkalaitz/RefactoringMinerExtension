package org.refactoringminer.test.python.refactorings;

import antlr.umladapter.PythonUMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.RenameClassRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.refactoringminer.utils.LangASTUtil.readResourceFile;

class RenameClassRefactoringDetectionTest {

    @Test
    void detectsClassRename_FromResourceFiles() throws Exception {
        // Read files from resources
        String beforePythonCode = readResourceFile("python-samples/before/calculator.py");
        String afterPythonCode = readResourceFile("python-samples/after/calculator.py");

        Map<String, String> beforeFiles = Map.of("tests/before/calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/after/calculator.py", afterPythonCode);
        assertRenameClassRefactoringDetected(beforeFiles, afterFiles, "Calculator", "AdvancedCalculator");
    }


    @Test
    void detectsClassRename() throws Exception {
        System.out.println("\n");

        // BEFORE code (Calculator)
        String beforePythonCode = """
            class Calculator:
                def sum(self, x, y):
                    x = x + y
                    return x
            """;
        // AFTER code (AdvancedCalculator)
        String afterPythonCode = """
            class AdvancedCalculator:
                def sum(self, x, y):
                    x = x + y
                    return x
            """;

        Map<String, String> beforeFiles = Map.of("tests/before/calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/after/calculator.py", afterPythonCode);
        assertRenameClassRefactoringDetected(beforeFiles, afterFiles, "Calculator", "AdvancedCalculator");
    }

    @Test
    void detectsClassRename_GreeterToFriendlyGreeter() throws Exception {
        System.out.println("\n");

        String beforePythonCode = """
        class Greeter:
            def greet(self, name):
                return "Hello, " + name
        """;
        String afterPythonCode = """
        class FriendlyGreeter:
            def greet(self, name):
                return "Hello, " + name
        """;

        Map<String, String> beforeFiles = Map.of("tests/before/greeter.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/after/greeter.py", afterPythonCode);
        assertRenameClassRefactoringDetected(beforeFiles, afterFiles, "Greeter", "FriendlyGreeter");
    }

    @Test
    void detectsClassRename_AnimalToMammal() throws Exception {
        System.out.println("\n");
        String beforePythonCode = """
        class Animal:
            def speak(self):
                return "..."
        """;
        String afterPythonCode = """
        class Mammal:
            def speak(self):
                return "..."
        """;

        Map<String, String> beforeFiles = Map.of("tests/before/animal.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/after/animal.py", afterPythonCode);
        assertRenameClassRefactoringDetected(beforeFiles, afterFiles, "Animal", "Mammal");
    }

    @Test
    void detectsClassRename_WithForLoop() throws Exception {
        System.out.println("\n");

        // BEFORE code (DataProcessor)
        String beforePythonCode = """
                class DataProcessor:
                    def process_list(self, items):
                        result = 0
                        for item in items:
                            processed = item * 2
                            result.append(processed)
                        return result
                
                    def calculate_sum(self, numbers):
                        total = 0
                        for number in numbers:
                            total += number
                        return total
                """;

        // AFTER code (DataHandler)
        String afterPythonCode = """
                class DataHandler:
                    def process_list(self, items):
                        result = 0
                        for item in items:
                            processed = item * 2
                        return result
                
                    def calculate_sum(self, numbers):
                        total = 0
                        for number in numbers:
                            total += number
                        return total
                """;

        Map<String, String> beforeFiles = Map.of("tests/before/data_processor.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/after/data_handler.py", afterPythonCode);
        assertRenameClassRefactoringDetected(beforeFiles, afterFiles, "DataProcessor", "DataHandler");
    }


    private void assertRenameClassRefactoringDetected(Map<String, String> beforeFiles, Map<String, String> afterFiles, String beforeClassName, String afterClassName) throws Exception {

        UMLModel beforeUML = new PythonUMLModelAdapter(beforeFiles).getUMLModel();
        UMLModel afterUML = new PythonUMLModelAdapter(afterFiles).getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);
        boolean classRenameDetected = diff.getRefactorings().stream()
                .anyMatch(ref -> ref instanceof RenameClassRefactoring &&
                        ((RenameClassRefactoring)ref).getOriginalClassName().equals(beforeClassName) &&
                        ((RenameClassRefactoring)ref).getRenamedClassName().equals(afterClassName));

        System.out.println("==== DIFF ====");
        System.out.println("Animal to Mammal");
        System.out.println("Class rename detected: " + classRenameDetected);
        System.out.println("Total refactorings: " + diff.getRefactorings().size());
        diff.getRefactorings().forEach(System.out::println);

        assertTrue(classRenameDetected, "Expected a RenameClassRefactoring from " + beforeClassName + " to " + afterClassName);
    }
}