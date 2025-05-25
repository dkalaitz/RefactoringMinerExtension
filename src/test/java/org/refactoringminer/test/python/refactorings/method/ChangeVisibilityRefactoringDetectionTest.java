package org.refactoringminer.test.python.refactorings.method;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.Visibility;
import gr.uom.java.xmi.diff.ChangeOperationAccessModifierRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.refactoringminer.test.python.refactorings.util.RefactoringAssertUtils.dumpOperation;

public class ChangeVisibilityRefactoringDetectionTest {

    @Test
    void detectsPublicToPrivateVisibilityChange() throws Exception {
        String beforePythonCode = """
            class VisibilityExample:
                def public_method(self):
                    return "I'm public"
            """;

        String afterPythonCode = """
            class VisibilityExample:
                def __public_method(self):
                    return "I'm now private"
            """;

        Map<String, String> beforeFiles = Map.of("tests/example.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/example.py", afterPythonCode);

        assertVisibilityChangeDetected(beforeFiles, afterFiles,
                "public_method", Visibility.PUBLIC,
                "public_method", Visibility.PRIVATE);
    }

    @Test
    void detectsProtectedToPublicVisibilityChange() throws Exception {
        String beforePythonCode = """
            class VisibilityExample:
                def _protected_method(self):
                    return "I'm protected"
            """;

        String afterPythonCode = """
            class VisibilityExample:
                def protected_method(self):
                    return "I'm now public"
            """;

        Map<String, String> beforeFiles = Map.of("tests/example.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/example.py", afterPythonCode);

        assertVisibilityChangeDetected(beforeFiles, afterFiles,
                "protected_method", Visibility.PROTECTED,
                "protected_method", Visibility.PUBLIC);
    }

    @Test
    void detectsPrivateToProtectedVisibilityChange() throws Exception {
        String beforePythonCode = """
            class VisibilityExample:
                def __private_method(self):
                    return "I'm private"
            """;

        String afterPythonCode = """
            class VisibilityExample:
                def _private_method(self):
                    return "I'm now protected"
            """;

        Map<String, String> beforeFiles = Map.of("tests/visibility/example.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/visibility/example.py", afterPythonCode);

        assertVisibilityChangeDetected(beforeFiles, afterFiles,
                "private_method", Visibility.PRIVATE,
                "private_method", Visibility.PROTECTED);
    }

    private void assertVisibilityChangeDetected(
            Map<String, String> beforeFiles,
            Map<String, String> afterFiles,
            String beforeName, Visibility beforeVisibility,
            String afterName, Visibility afterVisibility) throws Exception {

        UMLModel beforeUML = new UMLModelAdapter(beforeFiles).getUMLModel();
        UMLModel afterUML = new UMLModelAdapter(afterFiles).getUMLModel();

        // Print out all operations for debugging
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

        // Verify operations have expected visibility in the models
        Optional<UMLOperation> beforeOperation = findOperationByName(beforeUML, beforeName);
        Optional<UMLOperation> afterOperation = findOperationByName(afterUML, afterName);

        assertTrue(beforeOperation.isPresent(), "Operation not found in before model: " + beforeName);
        assertTrue(afterOperation.isPresent(), "Operation not found in after model: " + afterName);

        assertEquals(beforeVisibility, beforeOperation.get().getVisibility(),
                "Before operation should have " + beforeVisibility + " visibility");
        assertEquals(afterVisibility, afterOperation.get().getVisibility(),
                "After operation should have " + afterVisibility + " visibility");

        // Check for the visibility change refactoring
        UMLModelDiff diff = beforeUML.diff(afterUML);
        System.out.println("Refactoring type: " + diff.getRefactorings().get(0).getRefactoringType());
        boolean visibilityChangeDetected = diff.getRefactorings().stream()
                .anyMatch(ref -> {
                    if (ref instanceof ChangeOperationAccessModifierRefactoring visibilityRef) {
                        return visibilityRef.getOperationBefore().getName().equals(beforeName) &&
                                visibilityRef.getOperationAfter().getName().equals(afterName) &&
                                visibilityRef.getOperationBefore().getVisibility().equals(beforeVisibility) &&
                                visibilityRef.getOperationAfter().getVisibility().equals(afterVisibility);
                    }
                    return false;
                });

        System.out.println("==== DIFF ====");
        System.out.println("Visibility change detected: " + visibilityChangeDetected);
        System.out.println("Total refactorings: " + diff.getRefactorings().size());
        diff.getRefactorings().forEach(System.out::println);

        assertTrue(visibilityChangeDetected,
                "Expected visibility change refactoring from " + beforeVisibility +
                        " to " + afterVisibility + " for operation " + beforeName);
    }

    private Optional<UMLOperation> findOperationByName(UMLModel model, String operationName) {
        return model.getClassList().stream()
                .flatMap(umlClass -> umlClass.getOperations().stream())
                .filter(operation -> operation.getName().equals(operationName))
                .findFirst();
    }

}
