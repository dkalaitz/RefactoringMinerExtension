package org.refactoringminer.test.python.refactorings;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.refactoringminer.test.python.refactorings.util.RefactoringAssertUtils.assertRenameClassRefactoringDetected;
import static org.refactoringminer.utils.LangASTUtil.readResourceFile;

class RenameClassRefactoringDetectionTest {

    @Test
    void detectsClassRename_FromResourceFiles() throws Exception {
        // Read files from resources
        String beforePythonCode = readResourceFile("python-samples/before/calculator.py");
        String afterPythonCode = readResourceFile("python-samples/after/calculator.py");

        Map<String, String> beforeFiles = Map.of("tests/calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/calculator.py", afterPythonCode);
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

        Map<String, String> beforeFiles = Map.of("tests/calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/calculator.py", afterPythonCode);
        assertRenameClassRefactoringDetected(beforeFiles, afterFiles, "Calculator", "AdvancedCalculator");
    }

    @Test
    void detectsClassRenameWithImports() throws Exception {
        System.out.println("\n");

        // BEFORE code (Calculator with imports)
        String beforePythonCode = """
        import math
        from statistics import mean, median
        import numpy as np
        
        class Calculator:
            def sum(self, x, y):
                x = x + y
                return x
        """;

        // AFTER code (AdvancedCalculator with the same imports)
        String afterPythonCode = """
        import math
        from statistics import mean, median
        import numpy as np
        
        class AdvancedCalculator:
            def sum(self, x, y):
                x = x + y
                return x
        """;

        Map<String, String> beforeFiles = Map.of("tests/calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/calculator.py", afterPythonCode);
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

        Map<String, String> beforeFiles = Map.of("tests/greeter.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/greeter.py", afterPythonCode);
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

        Map<String, String> beforeFiles = Map.of("tests/animal.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/animal.py", afterPythonCode);
        assertRenameClassRefactoringDetected(beforeFiles, afterFiles, "Animal", "Mammal");
    }

    @Test
    void detectsClassRename_AnimalToMammal2() throws Exception {
        System.out.println("\n");
        String beforePythonCode = """
        class Animal:
            def speak(self, x):
                return "..."
        """;
        String afterPythonCode = """
        class Mammal:
            def speak(self, y):
                return "..."
        """;

        Map<String, String> beforeFiles = Map.of("tests/animal.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/animal.py", afterPythonCode);
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
                            total = total + number
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
                            total = total + number
                        return total
                """;

        Map<String, String> beforeFiles = Map.of("tests/data_processor.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/data_handler.py", afterPythonCode);
        assertRenameClassRefactoringDetected(beforeFiles, afterFiles, "DataProcessor", "DataHandler");
    }



}