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
public class RemoveParameterRefactoringDetectionTest {

    @Test
    void detectsRemoveParameter_UnusedParameter() throws Exception {
        String beforePythonCode = """
            class UserService:
                def create_user(self, name, email, age):
                    return User(name, email)
                
                def update_user(self, user_id, name, legacy_field):
                    return self.repository.update(user_id, name)
            """;

        String afterPythonCode = """
            class UserService:
                def create_user(self, name, email):
                    return User(name, email)
                
                def update_user(self, user_id, name):
                    return self.repository.update(user_id, name)
            """;

        Map<String, String> beforeFiles = Map.of("user_service.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("user_service.py", afterPythonCode);

        assertRemoveParameterRefactoringDetected(beforeFiles, afterFiles,
                "age", "create_user", "UserService");
    }

    @Test
    void detectsRemoveParameter_DefaultParameterRemoval() throws Exception {
        String beforePythonCode = """
            def format_text(text, font_size=12, color="black", bold=False):
                if bold:
                    text = f"<b>{text}</b>"
                return f"<span style='font-size:{font_size}px; color:{color}'>{text}</span>"
            """;

        String afterPythonCode = """
            def format_text(text, font_size=12, color="black"):
                return f"<span style='font-size:{font_size}px; color:{color}'>{text}</span>"
            """;

        Map<String, String> beforeFiles = Map.of("formatter.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("formatter.py", afterPythonCode);

        assertRemoveParameterRefactoringDetected(beforeFiles, afterFiles,
                "bold", "format_text", "");
    }

    public static void assertRemoveParameterRefactoringDetected(
            Map<String, String> beforeFiles,
            Map<String, String> afterFiles,
            String removedParameterName,
            String methodName,
            String className
    ) throws Exception {
        UMLModelAdapter beforeAdapter = new UMLModelAdapter(beforeFiles);
        UMLModelAdapter afterAdapter = new UMLModelAdapter(afterFiles);

        UMLModel beforeUML = beforeAdapter.getUMLModel();
        UMLModel afterUML = afterAdapter.getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);
        List<Refactoring> refactorings = diff.getRefactorings();

        System.out.println("\n=== REMOVE PARAMETER TEST: " + removedParameterName + " ===");
        System.out.println("Removed parameter: " + removedParameterName);
        System.out.println("Method: " + methodName + (className.isEmpty() ? " (module level)" : " in class " + className));
        System.out.println("Total refactorings detected: " + refactorings.size());

        boolean removeParameterFound = refactorings.stream()
                .anyMatch(r -> RefactoringType.REMOVE_PARAMETER.equals(r.getRefactoringType()) &&
                        r.toString().contains(removedParameterName) &&
                        r.toString().contains(methodName));

        if (!removeParameterFound) {
            System.out.println("Available refactorings:");
            refactorings.forEach(r -> System.out.println("  " + r.getRefactoringType() + ": " + r.toString()));

            fail("Expected remove parameter refactoring for parameter '" + removedParameterName +
                    "' in method '" + methodName + "' was not detected");
        }

        assertTrue(removeParameterFound, "Expected Remove Parameter refactoring to be detected");
    }
}