package org.refactoringminer.test.python.refactorings.method;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.refactoringminer.api.Refactoring;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Isolated
public class ExtractAndMoveMethodRefactoringDetectionTest {

    @Test
    void detectsExtractAndMoveMethod_CalculateToMathUtils() throws Exception {
        String beforePythonCode = """
            class Calculator:
                def add(self, x, y):
                    result = x + y
                    return result
                
                def multiply(self, x, y):
                    result = x * y
                    return result
            """;

        String afterCalculatorCode = """
            class Calculator:
                def add(self, x, y):
                    return MathUtils.calculate_sum(x, y)
                
                def multiply(self, x, y):
                    result = x * y
                    return result
            """;

        String afterMathUtilsCode = """
            class MathUtils:
                @staticmethod
                def calculate_sum(x, y):
                    result = x + y
                    return result
            """;

        Map<String, String> beforeFiles = Map.of("calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of(
                "calculator.py", afterCalculatorCode,
                "math_utils.py", afterMathUtilsCode
        );

        assertExtractAndMoveMethodRefactoringDetected(beforeFiles, afterFiles, "add", "calculate_sum", "Calculator", "MathUtils");
    }

    @Test
    void detectsExtractAndMoveMethod_ProcessToHelper() throws Exception {
        String beforePythonCode = """
            class DataProcessor:
                def process_data(self, data):
                    cleaned = data.strip()
                    return cleaned.upper()
            """;

        String afterProcessorCode = """
            class DataProcessor:
                def process_data(self, data):
                    return Helper.clean_and_format(data)
            """;

        String afterHelperCode = """
            class Helper:
                def clean_and_format(self, data):
                    cleaned = data.strip()
                    return cleaned.upper()
            """;

        Map<String, String> beforeFiles = Map.of("processor.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of(
                "processor.py", afterProcessorCode,
                "helper.py", afterHelperCode
        );

        assertExtractAndMoveMethodRefactoringDetected(beforeFiles, afterFiles, "process_data", "clean_and_format", "DataProcessor", "Helper");
    }

    @Test
    void detectsExtractAndMoveMethod_ValidateToValidator() throws Exception {
        String beforePythonCode = """
            class User:
                def __init__(self, email):
                    self.email = email
                
                def validate_email(self):
                    return "@" in self.email
            """;

        String afterUserCode = """
            class User:
                def __init__(self, email):
                    self.email = email
                
                def validate_email(self):
                    return EmailValidator.is_valid(self.email)
            """;

        String afterValidatorCode = """
            class EmailValidator:
                @staticmethod
                def is_valid(email):
                    return "@" in email
            """;

        Map<String, String> beforeFiles = Map.of("user.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of(
                "user.py", afterUserCode,
                "validator.py", afterValidatorCode
        );

        assertExtractAndMoveMethodRefactoringDetected(beforeFiles, afterFiles, "validate_email", "is_valid", "User", "EmailValidator");
    }

    public static void assertExtractAndMoveMethodRefactoringDetected(
            Map<String, String> beforeFiles,
            Map<String, String> afterFiles,
            String originalMethodName,
            String extractedMethodName,
            String sourceClassName,
            String targetClassName
    ) throws Exception {
        UMLModelAdapter beforeAdapter = new UMLModelAdapter(beforeFiles);
        UMLModelAdapter afterAdapter = new UMLModelAdapter(afterFiles);

        UMLModel beforeUML = beforeAdapter.getUMLModel();
        UMLModel afterUML = afterAdapter.getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);
        List<Refactoring> refactorings = diff.getRefactorings();

        // Look for Extract Method refactoring
        boolean extractFound = refactorings.stream()
                .filter(r -> r instanceof ExtractOperationRefactoring)
                .map(r -> (ExtractOperationRefactoring) r)
                .anyMatch(refactoring -> {
                    String extractedName = refactoring.getExtractedOperation().getName();
                    String extractedClass = refactoring.getExtractedOperation().getClassName();
                    return extractedName.equals(extractedMethodName) &&
                            extractedClass.equals(targetClassName);
                });

        // Look for Move Method refactoring
        boolean moveFound = refactorings.stream()
                .filter(r -> r instanceof MoveOperationRefactoring)
                .map(r -> (MoveOperationRefactoring) r)
                .anyMatch(refactoring -> {
                    String originalName = refactoring.getOriginalOperation().getName();
                    String originalClass = refactoring.getOriginalOperation().getClassName();
                    String movedName = refactoring.getMovedOperation().getName();
                    String movedClass = refactoring.getMovedOperation().getClassName();
                    return originalName.equals(originalMethodName) &&
                            originalClass.equals(sourceClassName) &&
                            movedName.equals(extractedMethodName) &&
                            movedClass.equals(targetClassName);
                });

        boolean refactoringDetected = extractFound && moveFound;
        System.out.println("Refactorings detected: " + refactorings.size());

        if (!refactoringDetected) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Extract and Move Method refactoring not detected.\n");
            errorMessage.append("Expected: Extract/Move method '").append(originalMethodName)
                    .append("' from class '").append(sourceClassName)
                    .append("' to method '").append(extractedMethodName)
                    .append("' in class '").append(targetClassName).append("'\n");

            errorMessage.append("Detected refactorings:\n");
            for (Refactoring refactoring : refactorings) {
                errorMessage.append("  - ").append(refactoring.getName()).append(": ")
                        .append(refactoring.toString()).append("\n");
            }

            fail(errorMessage.toString());
        }

        assertTrue(refactoringDetected, "Expected Extract and Move Method refactoring to be detected");
    }
}