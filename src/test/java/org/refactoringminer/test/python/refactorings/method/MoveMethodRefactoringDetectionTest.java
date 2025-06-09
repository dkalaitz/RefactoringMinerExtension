package org.refactoringminer.test.python.refactorings.method;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.refactoringminer.utils.RefactoringAssertUtils.dumpOperation;

class MoveMethodRefactoringDetectionTest {

    @Test
    void detectsMethodMove_CalculateFormula_FromCalculatorToMathHelper() throws Exception {
        // BEFORE: Method is in the Calculator class
        String beforePythonCode1 = """
            class Calculator:
                def add(self, x, y):
                    return x + y
                    
                def calculate_formula(self, a, b, c):
                    result = a * b + c
                    return result
            """;

        String beforePythonCode2 = """
            class MathHelper:
                def square(self, x):
                    return x * x
            """;

        // AFTER: Method is moved to MathHelper class
        String afterPythonCode1 = """
            class Calculator:
                def add(self, x, y):
                    return x + y
            """;

        String afterPythonCode2 = """
            class MathHelper:
                def square(self, x):
                    return x * x
                    
                def calculate_formula(self, a, b, c):
                    result = a * b + c
                    return result
            """;

        Map<String, String> beforeFiles = Map.of(
                "tests/before/calculator.py", beforePythonCode1,
                "tests/before/math_helper.py", beforePythonCode2
        );

        Map<String, String> afterFiles = Map.of(
                "tests/after/calculator.py", afterPythonCode1,
                "tests/after/math_helper.py", afterPythonCode2
        );

        assertMoveOperationRefactoringDetected(
                beforeFiles,
                afterFiles,
                "Calculator",
                "MathHelper",
                "calculate_formula"
        );
    }

    @Test
    void detectsMethodMove_ProcessData_FromProcessorToHandler() throws Exception {
        // BEFORE: Method is in the DataProcessor class
        String beforePythonCode1 = """
            class DataProcessor:
                def sanitize(self, data):
                    return data.strip()
                    
                def process_data(self, data_list):
                    results = []
                    for item in data_list:
                        processed = item.upper()
                        results.append(processed)
                    return results
            """;

        String beforePythonCode2 = """
            class DataHandler:
                def validate(self, data):
                    return len(data) > 0
            """;

        // AFTER: Method is moved to DataHandler class
        String afterPythonCode1 = """
            class DataProcessor:
                def sanitize(self, data):
                    return data.strip()
            """;

        String afterPythonCode2 = """
            class DataHandler:
                def validate(self, data):
                    return len(data) > 0
                    
                def process_data(self, data_list):
                    results = []
                    for item in data_list:
                        processed = item.upper()
                        results.append(processed)
                    return results
            """;

        Map<String, String> beforeFiles = Map.of(
                "tests/before/processor.py", beforePythonCode1,
                "tests/before/handler.py", beforePythonCode2
        );

        Map<String, String> afterFiles = Map.of(
                "tests/after/processor.py", afterPythonCode1,
                "tests/after/handler.py", afterPythonCode2
        );

        assertMoveOperationRefactoringDetected(
                beforeFiles,
                afterFiles,
                "DataProcessor",
                "DataHandler",
                "process_data"
        );
    }

    private void assertMoveOperationRefactoringDetected(
            Map<String, String> beforeFiles,
            Map<String, String> afterFiles,
            String sourceClassName,
            String targetClassName,
            String methodName) throws Exception {

        UMLModel beforeUML = new UMLModelAdapter(beforeFiles).getUMLModel();
        UMLModel afterUML = new UMLModelAdapter(afterFiles).getUMLModel();

        System.out.println("=== BEFORE MODEL OPERATIONS ===");
        beforeUML.getClassList().forEach(umlClass -> {
            System.out.println("Class: " + umlClass.getName());
            for (UMLOperation op : umlClass.getOperations()) {
                System.out.println("  " + dumpOperation(op));
            }
        });

        System.out.println("=== AFTER MODEL OPERATIONS ===");
        afterUML.getClassList().forEach(umlClass -> {
            System.out.println("Class: " + umlClass.getName());
            for (UMLOperation op : umlClass.getOperations()) {
                System.out.println("  " + dumpOperation(op));
            }
        });

        UMLModelDiff diff = beforeUML.diff(afterUML);
        boolean moveMethodDetected = diff.getRefactorings().stream()
                .anyMatch(ref -> {
                    if (ref instanceof MoveOperationRefactoring moveRef) {
                        UMLOperation originalOperation = moveRef.getOriginalOperation();
                        UMLOperation movedOperation = moveRef.getMovedOperation();

                        return originalOperation.getName().equals(methodName) &&
                                originalOperation.getClassName().equals(sourceClassName) &&
                                movedOperation.getName().equals(methodName) &&
                                movedOperation.getClassName().equals(targetClassName);
                    }
                    return false;
                });

        System.out.println("==== DIFF ====");
        System.out.println("Move method detected: " + moveMethodDetected);
        System.out.println("Total refactorings: " + diff.getRefactorings().size());

        diff.getRefactorings().forEach(System.out::println);
        System.out.println("\n");

        assertTrue(moveMethodDetected,
                String.format("Expected a MoveOperationRefactoring of method '%s' from class '%s' to class '%s'",
                        methodName, sourceClassName, targetClassName));
    }
}