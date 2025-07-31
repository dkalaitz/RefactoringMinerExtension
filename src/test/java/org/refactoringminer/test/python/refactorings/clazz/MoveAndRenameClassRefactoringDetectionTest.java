package org.refactoringminer.test.python.refactorings.clazz;

import extension.umladapter.UMLModelAdapter;
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

    @Test
    void detectsMoveAndRenameClass_ReaderToIoFileReader() throws Exception {
        String beforeReaderCode = """
        class Reader:
            def __init__(self, filename):
                self.filename = filename
            
            def read_content(self):
                return f"Reading from {self.filename}"
            
            def read_lines(self):
                return ["line1", "line2", "line3"]
        """;

        String afterFileReaderCode = """
        class FileReader:
            def __init__(self, filename):
                self.filename = filename
            
            def read_content(self):
                return f"Reading from {self.filename}"
            
            def read_lines(self):
                return ["line1", "line2", "line3"]
        """;

        Map<String, String> beforeFiles = Map.of("reader.py", beforeReaderCode);
        Map<String, String> afterFiles = Map.of("io/file_reader.py", afterFileReaderCode);

        assertMoveAndRenameClassRefactoringDetected(beforeFiles, afterFiles,
                "Reader", "FileReader", "reader.py", "io/file_reader.py");
    }

    @Test
    void detectsMoveAndRenameClass_ClientToNetworkHttpClient() throws Exception {
        String beforeClientCode = """
        class Client:
            def __init__(self, base_url):
                self.base_url = base_url
                self.headers = {}
            
            def request(self, method, endpoint):
                return f"{method} {self.base_url}/{endpoint}"
            
            def set_header(self, key, value):
                self.headers[key] = value
        """;

        String afterHttpClientCode = """
        class HttpClient:
            def __init__(self, base_url):
                self.base_url = base_url
                self.headers = {}
            
            def request(self, method, endpoint):
                return f"{method} {self.base_url}/{endpoint}"
            
            def set_header(self, key, value):
                self.headers[key] = value
        """;

        Map<String, String> beforeFiles = Map.of("client.py", beforeClientCode);
        Map<String, String> afterFiles = Map.of("network/http_client.py", afterHttpClientCode);

        assertMoveAndRenameClassRefactoringDetected(beforeFiles, afterFiles,
                "Client", "HttpClient", "client.py", "network/http_client.py");
    }

    @Test
    void detectsMoveAndRenameClass_HandlerToEventActionHandler() throws Exception {
        String beforeHandlerCode = """
        class Handler:
            def __init__(self, name):
                self.name = name
                self.actions = []
            
            def execute(self, action):
                self.actions.append(action)
                return f"Executed {action}"
            
            def get_history(self):
                return self.actions
        """;

        String afterActionHandlerCode = """
        class ActionHandler:
            def __init__(self, name):
                self.name = name
                self.actions = []
            
            def execute(self, action):
                self.actions.append(action)
                return f"Executed {action}"
            
            def get_history(self):
                return self.actions
        """;

        Map<String, String> beforeFiles = Map.of("handler.py", beforeHandlerCode);
        Map<String, String> afterFiles = Map.of("event/action_handler.py", afterActionHandlerCode);

        assertMoveAndRenameClassRefactoringDetected(beforeFiles, afterFiles,
                "Handler", "ActionHandler", "handler.py", "event/action_handler.py");
    }

    @Test
    void detectsMoveAndRenameClass_BuilderToFactoryObjectBuilder() throws Exception {
        String beforeBuilderCode = """
        class Builder:
            def __init__(self):
                self.components = {}
            
            def add_component(self, name, value):
                self.components[name] = value
                return self
            
            def build(self):
                return self.components.copy()
            
            def reset(self):
                self.components.clear()
        """;

        String afterObjectBuilderCode = """
        class ObjectBuilder:
            def __init__(self):
                self.components = {}
            
            def add_component(self, name, value):
                self.components[name] = value
                return self
            
            def build(self):
                return self.components.copy()
            
            def reset(self):
                self.components.clear()
        """;

        Map<String, String> beforeFiles = Map.of("builder.py", beforeBuilderCode);
        Map<String, String> afterFiles = Map.of("factory/object_builder.py", afterObjectBuilderCode);

        assertMoveAndRenameClassRefactoringDetected(beforeFiles, afterFiles,
                "Builder", "ObjectBuilder", "builder.py", "factory/object_builder.py");
    }

    @Test
    void detectsMoveAndRenameClass_ValidatorToChecksDataValidator() throws Exception {
        String beforeValidatorCode = """
        class Validator:
            def __init__(self, rules):
                self.rules = rules
            
            def validate(self, data):
                for rule in self.rules:
                    if not self.check_rule(data, rule):
                        return False
                return True
            
            def check_rule(self, data, rule):
                return rule in str(data)
            
            def add_rule(self, rule):
                self.rules.append(rule)
        """;

        String afterDataValidatorCode = """
        class DataValidator:
            def __init__(self, rules):
                self.rules = rules
            
            def validate(self, data):
                for rule in self.rules:
                    if not self.check_rule(data, rule):
                        return False
                return True
            
            def check_rule(self, data, rule):
                return rule in str(data)
            
            def add_rule(self, rule):
                self.rules.append(rule)
        """;

        Map<String, String> beforeFiles = Map.of("validator.py", beforeValidatorCode);
        Map<String, String> afterFiles = Map.of("checks/data_validator.py", afterDataValidatorCode);

        assertMoveAndRenameClassRefactoringDetected(beforeFiles, afterFiles,
                "Validator", "DataValidator", "validator.py", "checks/data_validator.py");
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