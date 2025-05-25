package org.refactoringminer.test.python.refactorings;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.refactoringminer.api.Refactoring;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.refactoringminer.test.python.refactorings.util.RefactoringAssertUtils.dumpOperation;

class RenameMethodRefactoringDetectionTest {

    @Test
    void detectsMethodRename_SumToAdd() throws Exception {
        String beforePythonCode = """
            class Calculator:
                def sum(self, x, y):
                    return x + y
            """;
        String afterPythonCode = """
            class Calculator:
                def add(self, x, y):
                    return x + y
            """;
        Map<String, String> beforeFiles = Map.of("tests/calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/calculator.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "sum", "add");
    }

    @Test
    void detectsMethodRename_GreetToSayHello() throws Exception {
        String beforePythonCode = """
            class Greeter:
                def greet(self, name):
                    return "Hello, " + name
            """;
        String afterPythonCode = """
            class Greeter:
                def say_hello(self, name):
                    return "Hello, " + name
            """;
        Map<String, String> beforeFiles = Map.of("tests/greeter.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/greeter.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "greet", "say_hello");
    }

    @Test
    void detectsMethodRename_SpeakToCommunicate() throws Exception {
        String beforePythonCode = """
            class Animal:
                def speak(self):
                    return "noise"
            """;
        String afterPythonCode = """
            class Animal:
                def communicate(self):
                    return "noise"
            """;
        Map<String, String> beforeFiles = Map.of("tests/animal.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/animal.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "speak", "communicate");
    }

    @Test
    void detectsMethodRename_DataProcessorCalculateSum_ToCalculateAdd() throws Exception {
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
                class DataProcessor:
                    def process_list1(self, items):
                        result = 0
                        for item in items:
                            processed = item * 2
                        return result
                
                    def calculate_add(self, numbers):
                        total = 0
                        for number in numbers:
                            total = total + number
                        return total
                """;
        Map<String, String> beforeFiles = Map.of("tests/dataprocessor.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/dataprocessor.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "process_list", "process_list1");
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "calculate_sum", "calculate_add");
        // TODO: Assert total refactorings
    }

    private void assertRenameOperationRefactoringDetected(Map<String, String> beforeFiles, Map<String, String> afterFiles, String beforeName, String afterName) throws Exception {
        UMLModel beforeUML = new UMLModelAdapter(beforeFiles).getUMLModel();
        UMLModel afterUML = new UMLModelAdapter(afterFiles).getUMLModel();


        UMLModelDiff diff = beforeUML.diff(afterUML);
        diff.getRefactorings().forEach(refactoring -> System.out.println(refactoring.getName()));
        boolean methodRenameDetected = diff.getRefactorings().stream()
                .anyMatch(ref -> {
                    if (ref instanceof RenameOperationRefactoring renameRef) {
                        UMLOperation originalOperation = renameRef.getOriginalOperation();
                        System.out.println("Original operation: " + originalOperation.getName());
                        UMLOperation renamedOperation = renameRef.getRenamedOperation();
                        System.out.println("Renamed operation: " + renamedOperation.getName());

                        return originalOperation.getName().equals(beforeName) &&
                                renamedOperation.getName().equals(afterName);
                    }
                    return false;
                });

        assertTrue(methodRenameDetected, "Expected a RenameMethodRefactoring from " + beforeName + " to " + afterName);
    }


}