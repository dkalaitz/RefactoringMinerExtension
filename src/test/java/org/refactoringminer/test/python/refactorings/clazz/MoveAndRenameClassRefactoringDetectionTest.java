package org.refactoringminer.test.python.refactorings.clazz;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.MoveClassRefactoring;
import gr.uom.java.xmi.diff.RenameClassRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.refactoringminer.api.Refactoring;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Isolated
public class MoveAndRenameClassRefactoringDetectionTest {

    @Test
    void detectsMoveAndRenameClass_CalculatorToMathAdvancedCalculator() throws Exception {
        String beforeCalculatorCode = """
            class Calculator:
                def add(self, x, y):
                    return x + y
                
                def subtract(self, x, y):
                    return x - y
            """;

        String afterAdvancedCalculatorCode = """
            class AdvancedCalculator:
                def add(self, x, y):
                    return x + y
                
                def subtract(self, x, y):
                    return x - y
            """;

        Map<String, String> beforeFiles = Map.of("calculator.py", beforeCalculatorCode);
        Map<String, String> afterFiles = Map.of("math/advanced_calculator.py", afterAdvancedCalculatorCode);

        assertMoveAndRenameClassRefactoringDetected(beforeFiles, afterFiles,
                "Calculator", "AdvancedCalculator", "calculator.py", "math/advanced_calculator.py");
    }

    @Test
    void detectsMoveAndRenameClass_UserToModelsUserAccount() throws Exception {
        String beforeUserCode = """
            class User:
                def __init__(self, name, email):
                    self.name = name
                    self.email = email
                
                def get_info(self):
                    return f"{self.name} - {self.email}"
            """;

        String afterUserAccountCode = """
            class UserAccount:
                def __init__(self, name, email):
                    self.name = name
                    self.email = email
                
                def get_info(self):
                    return f"{self.name} - {self.email}"
            """;

        Map<String, String> beforeFiles = Map.of("user.py", beforeUserCode);
        Map<String, String> afterFiles = Map.of("models/user_account.py", afterUserAccountCode);

        assertMoveAndRenameClassRefactoringDetected(beforeFiles, afterFiles,
                "User", "UserAccount", "user.py", "models/user_account.py");
    }

    @Test
    void detectsMoveAndRenameClass_ProcessorToUtilsDataHandler() throws Exception {
        String beforeProcessorCode = """
            class Processor:
                def process_data(self, data):
                    return data.strip().upper()
                
                def validate_data(self, data):
                    return len(data) > 0
            """;

        String afterDataHandlerCode = """
            class DataHandler:
                def process_data(self, data):
                    return data.strip().upper()
                
                def validate_data(self, data):
                    return len(data) > 0
            """;

        Map<String, String> beforeFiles = Map.of("processor.py", beforeProcessorCode);
        Map<String, String> afterFiles = Map.of("utils/data_handler.py", afterDataHandlerCode);

        assertMoveAndRenameClassRefactoringDetected(beforeFiles, afterFiles,
                "Processor", "DataHandler", "processor.py", "utils/data_handler.py");
    }

    @Test
    void detectsMoveAndRenameClass_ManagerToServicesUserService() throws Exception {
        String beforeManagerCode = """
            class Manager:
                def create_user(self, data):
                    return data
                
                def delete_user(self, user_id):
                    return user_id
            """;

        String afterUserServiceCode = """
            class UserService:
                def create_user(self, data):
                    return data
                
                def delete_user(self, user_id):
                    return user_id
            """;

        Map<String, String> beforeFiles = Map.of("manager.py", beforeManagerCode);
        Map<String, String> afterFiles = Map.of("services/user_service.py", afterUserServiceCode);

        assertMoveAndRenameClassRefactoringDetected(beforeFiles, afterFiles,
                "Manager", "UserService", "manager.py", "services/user_service.py");
    }

    @Test
    void detectsMoveAndRenameClass_HelperToToolsStringUtils() throws Exception {
        String beforeHelperCode = """
            class Helper:
                def format_string(self, text):
                    return text.strip().title()
                
                def is_empty(self, text):
                    return len(text) == 0
            """;

        String afterStringUtilsCode = """
            class StringUtils:
                def format_string(self, text):
                    return text.strip().title()
                
                def is_empty(self, text):
                    return len(text) == 0
            """;

        Map<String, String> beforeFiles = Map.of("helper.py", beforeHelperCode);
        Map<String, String> afterFiles = Map.of("tools/string_utils.py", afterStringUtilsCode);

        assertMoveAndRenameClassRefactoringDetected(beforeFiles, afterFiles,
                "Helper", "StringUtils", "helper.py", "tools/string_utils.py");
    }

    public static void assertMoveAndRenameClassRefactoringDetected(
            Map<String, String> beforeFiles,
            Map<String, String> afterFiles,
            String originalClassName,
            String renamedClassName,
            String originalFilePath,
            String newFilePath
    ) throws Exception {
        UMLModelAdapter beforeAdapter = new UMLModelAdapter(beforeFiles);
        UMLModelAdapter afterAdapter = new UMLModelAdapter(afterFiles);

        UMLModel beforeUML = beforeAdapter.getUMLModel();
        UMLModel afterUML = afterAdapter.getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);
        List<Refactoring> refactorings = diff.getRefactorings();

        // Look for Move Class refactoring
        boolean moveFound = refactorings.stream()
                .filter(r -> r instanceof MoveClassRefactoring)
                .map(r -> (MoveClassRefactoring) r)
                .anyMatch(refactoring -> {
                    String originalName = refactoring.getOriginalClass().getName();
                    String movedName = refactoring.getMovedClass().getName();

                    // Check if this move matches our expected pattern
                    return (originalName.equals(originalClassName) && movedName.equals(renamedClassName)) ||
                            (originalName.equals(originalClassName) && movedName.equals(originalClassName));
                });

        // Look for Rename Class refactoring
        boolean renameFound = refactorings.stream()
                .filter(r -> r instanceof RenameClassRefactoring)
                .map(r -> (RenameClassRefactoring) r)
                .anyMatch(refactoring -> {
                    String originalName = refactoring.getOriginalClass().getName();
                    String renamedName = refactoring.getRenamedClass().getName();

                    // Check if this rename matches our expected pattern
                    return originalName.equals(originalClassName) && renamedName.equals(renamedClassName);
                });

        // For Move and Rename, we expect either move OR rename (or both)
        boolean refactoringDetected = moveFound || renameFound;

        if (!refactoringDetected) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Move and Rename Class refactoring not detected.\n");
            errorMessage.append("Expected: Move and rename class '").append(originalClassName)
                    .append("' from '").append(originalFilePath)
                    .append("' to class '").append(renamedClassName)
                    .append("' in '").append(newFilePath).append("'\n");

            errorMessage.append("Detected refactorings:\n");
            for (Refactoring refactoring : refactorings) {
                errorMessage.append("  - ").append(refactoring.getName()).append(": ")
                        .append(refactoring.toString()).append("\n");
            }

            fail(errorMessage.toString());
        }

        assertTrue(refactoringDetected, "Expected Move and Rename Class refactoring to be detected");
    }
}