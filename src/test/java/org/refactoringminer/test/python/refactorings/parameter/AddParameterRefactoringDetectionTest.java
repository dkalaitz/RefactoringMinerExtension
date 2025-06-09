package org.refactoringminer.test.python.refactorings.parameter;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Isolated
public class AddParameterRefactoringDetectionTest {

    @Test
    void detectsAddParameter_SimpleAddition() throws Exception {
        String beforePythonCode = """
            class UserService:
                def create_user(self, name):
                    return User(name)
                
                def greet_user(self, user):
                    return f"Hello {user.name}!"
            """;

        String afterPythonCode = """
            class UserService:
                def create_user(self, name, email):
                    return User(name, email)
                
                def greet_user(self, user, greeting="Hello"):
                    return f"{greeting} {user.name}!"
            """;

        Map<String, String> beforeFiles = Map.of("user_service.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("user_service.py", afterPythonCode);

        assertAddParameterRefactoringDetected(beforeFiles, afterFiles,
                "email", "create_user", "UserService");
    }

    @Test
    void detectsAddParameter_DefaultValueParameter() throws Exception {
        String beforePythonCode = """
            def calculate_area(width, height):
                return width * height
            """;

        String afterPythonCode = """
            def calculate_area(width, height, unit="square_meters"):
                result = width * height
                return f"{result} {unit}"
            """;

        Map<String, String> beforeFiles = Map.of("calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("calculator.py", afterPythonCode);

        assertAddParameterRefactoringDetected(beforeFiles, afterFiles,
                "unit", "calculate_area", "");
    }

    public static void assertAddParameterRefactoringDetected(
            Map<String, String> beforeFiles,
            Map<String, String> afterFiles,
            String addedParameterName,
            String methodName,
            String className
    ) throws Exception {
        UMLModelAdapter beforeAdapter = new UMLModelAdapter(beforeFiles);
        UMLModelAdapter afterAdapter = new UMLModelAdapter(afterFiles);

        UMLModel beforeUML = beforeAdapter.getUMLModel();
        UMLModel afterUML = afterAdapter.getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);
        List<Refactoring> refactorings = diff.getRefactorings();

        System.out.println("\n=== ADD PARAMETER TEST: " + addedParameterName + " ===");
        System.out.println("Added parameter: " + addedParameterName);
        System.out.println("Method: " + methodName + (className.isEmpty() ? " (module level)" : " in class " + className));
        System.out.println("Total refactorings detected: " + refactorings.size());

        boolean addParameterFound = refactorings.stream()
                .anyMatch(r -> RefactoringType.ADD_PARAMETER.equals(r.getRefactoringType()) &&
                        r.toString().contains(addedParameterName) &&
                        r.toString().contains(methodName));

        if (!addParameterFound) {
            System.out.println("Available refactorings:");
            refactorings.forEach(r -> System.out.println("  " + r.getRefactoringType() + ": " + r.toString()));

            fail("Expected add parameter refactoring for parameter '" + addedParameterName +
                    "' in method '" + methodName + "' was not detected");
        }

        assertTrue(addParameterFound, "Expected Add Parameter refactoring to be detected");
    }
}