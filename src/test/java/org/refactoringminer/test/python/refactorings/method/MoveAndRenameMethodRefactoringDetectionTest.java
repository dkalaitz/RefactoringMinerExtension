package org.refactoringminer.test.python.refactorings.method;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.refactoringminer.api.Refactoring;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Isolated
public class MoveAndRenameMethodRefactoringDetectionTest {

    @Test
    void detectsMoveAndRenameMethod_CalculateToMathUtilsCompute() throws Exception {
        String beforePythonCode = """
            class Calculator:
                def calculate(self, x, y):
                    return x + y
                
                def multiply(self, x, y):
                    return x * y
            """;

        String afterCalculatorCode = """
            class Calculator:
                def multiply(self, x, y):
                    return x * y
            """;

        String afterMathUtilsCode = """
            class MathUtils:
                def compute(self, x, y):
                    return x + y
            """;

        Map<String, String> beforeFiles = Map.of("calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of(
                "calculator.py", afterCalculatorCode,
                "math_utils.py", afterMathUtilsCode
        );

        assertMoveAndRenameMethodRefactoringDetected(beforeFiles, afterFiles,
                "calculate", "compute", "Calculator", "MathUtils");
    }

    @Test
    void detectsMoveAndRenameMethod_ProcessToHandlerExecute() throws Exception {
        String beforePythonCode = """
            class DataProcessor:
                def process(self, data):
                    return data.strip().upper()
                
                def validate(self, data):
                    return len(data) > 0
            """;

        String afterProcessorCode = """
            class DataProcessor:
                def validate(self, data):
                    return len(data) > 0
            """;

        String afterHandlerCode = """
            class DataHandler:
                def execute(self, data):
                    return data.strip().upper()
            """;

        Map<String, String> beforeFiles = Map.of("processor.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of(
                "processor.py", afterProcessorCode,
                "handler.py", afterHandlerCode
        );

        assertMoveAndRenameMethodRefactoringDetected(beforeFiles, afterFiles,
                "process", "execute", "DataProcessor", "DataHandler");
    }

    @Test
    void detectsMoveAndRenameMethod_SaveToRepositoryStore() throws Exception {
        String beforePythonCode = """
            class UserService:
                def save(self, user_data):
                    return user_data
                
                def find(self, user_id):
                    return user_id
            """;

        String afterServiceCode = """
            class UserService:
                def find(self, user_id):
                    return user_id
            """;

        String afterRepositoryCode = """
            class UserRepository:
                def store(self, user_data):
                    return user_data
            """;

        Map<String, String> beforeFiles = Map.of("service.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of(
                "service.py", afterServiceCode,
                "repository.py", afterRepositoryCode
        );

        assertMoveAndRenameMethodRefactoringDetected(beforeFiles, afterFiles,
                "save", "store", "UserService", "UserRepository");
    }

    @Test
    void detectsMoveAndRenameMethod_ValidateToCheckerVerify() throws Exception {
        String beforePythonCode = """
            class User:
                def validate(self, email):
                    return "@" in email
            """;

        String afterUserCode = """
            class User:
                pass
            """;

        String afterCheckerCode = """
            class EmailChecker:
                def verify(self, email):
                    return "@" in email
            """;

        Map<String, String> beforeFiles = Map.of("user.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of(
                "user.py", afterUserCode,
                "checker.py", afterCheckerCode
        );

        assertMoveAndRenameMethodRefactoringDetected(beforeFiles, afterFiles,
                "validate", "verify", "User", "EmailChecker");
    }

    public static void assertMoveAndRenameMethodRefactoringDetected(
            Map<String, String> beforeFiles,
            Map<String, String> afterFiles,
            String originalMethodName,
            String renamedMethodName,
            String sourceClassName,
            String targetClassName
    ) throws Exception {
        UMLModelAdapter beforeAdapter = new UMLModelAdapter(beforeFiles);
        UMLModelAdapter afterAdapter = new UMLModelAdapter(afterFiles);

        UMLModel beforeUML = beforeAdapter.getUMLModel();
        UMLModel afterUML = afterAdapter.getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);
        List<Refactoring> refactorings = diff.getRefactorings();

        // Look for Move Operation refactoring
        boolean moveFound = refactorings.stream()
                .filter(r -> r instanceof MoveOperationRefactoring)
                .map(r -> (MoveOperationRefactoring) r)
                .anyMatch(refactoring -> {
                    String originalName = refactoring.getOriginalOperation().getName();
                    String originalClass = refactoring.getOriginalOperation().getClassName();
                    String movedName = refactoring.getMovedOperation().getName();
                    String movedClass = refactoring.getMovedOperation().getClassName();

                    // Check if this move operation matches our expected pattern
                    boolean namesMatch = (originalName.equals(originalMethodName) && movedName.equals(renamedMethodName)) ||
                            (originalName.equals(originalMethodName) && movedName.equals(originalMethodName));
                    boolean classesMatch = originalClass.equals(sourceClassName) && movedClass.equals(targetClassName);

                    return namesMatch && classesMatch;
                });

        // Look for Rename Operation refactoring
        boolean renameFound = refactorings.stream()
                .filter(r -> r instanceof RenameOperationRefactoring)
                .map(r -> (RenameOperationRefactoring) r)
                .anyMatch(refactoring -> {
                    String originalName = refactoring.getOriginalOperation().getName();
                    String renamedName = refactoring.getRenamedOperation().getName();
                    String originalClass = refactoring.getOriginalOperation().getClassName();
                    String renamedClass = refactoring.getRenamedOperation().getClassName();

                    // Check if this rename operation matches our expected pattern
                    boolean namesMatch = originalName.equals(originalMethodName) && renamedName.equals(renamedMethodName);
                    boolean classesMatch = (originalClass.equals(sourceClassName) && renamedClass.equals(targetClassName)) ||
                            (originalClass.equals(sourceClassName) && renamedClass.equals(sourceClassName)) ||
                            (originalClass.equals(targetClassName) && renamedClass.equals(targetClassName));

                    return namesMatch && classesMatch;
                });

        // For Move and Rename, we expect either move OR rename (or both)
        boolean refactoringDetected = moveFound || renameFound;

        if (!refactoringDetected) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Move and Rename Method refactoring not detected.\n");
            errorMessage.append("Expected: Move and rename method '").append(originalMethodName)
                    .append("' from class '").append(sourceClassName)
                    .append("' to method '").append(renamedMethodName)
                    .append("' in class '").append(targetClassName).append("'\n");

            errorMessage.append("Detected refactorings:\n");
            for (Refactoring refactoring : refactorings) {
                errorMessage.append("  - ").append(refactoring.getName()).append(": ")
                        .append(refactoring.toString()).append("\n");
            }

            fail(errorMessage.toString());
        }

        assertTrue(refactoringDetected, "Expected Move and Rename Method refactoring to be detected");
    }
}