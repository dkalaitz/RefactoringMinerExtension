package org.refactoringminer.test.python.refactorings.attribute;

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
public class RenameAttributeRefactoringDetectionTest {

    @Test
    void detectsRenameAttribute_SimpleRename() throws Exception {
        String beforePythonCode = """
            class Person:
                def __init__(self, name):
                    self.full_name = name
                
                def get_info(self):
                    return f"Person: {self.full_name}"
                
                def update_name(self, new_name):
                    self.full_name = new_name
            """;

        String afterPythonCode = """
            class Person:
                def __init__(self, name):
                    self.name = name
                
                def get_info(self):
                    return f"Person: {self.name}"
                
                def update_name(self, new_name):
                    self.name = new_name
            """;

        Map<String, String> beforeFiles = Map.of("person.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("person.py", afterPythonCode);

        assertRenameAttributeRefactoringDetected(beforeFiles, afterFiles,
                "full_name", "name", "Person");
    }

    @Test
    void detectsRenameAttribute_PrivateToPublic() throws Exception {
        String beforePythonCode = """
            class BankAccount:
                def __init__(self, balance):
                    self._balance = balance
                
                def get_balance(self):
                    return self._balance
                
                def deposit(self, amount):
                    self._balance += amount
            """;

        String afterPythonCode = """
            class BankAccount:
                def __init__(self, balance):
                    self.balance = balance
                
                def get_balance(self):
                    return self.balance
                
                def deposit(self, amount):
                    self.balance += amount
            """;

        Map<String, String> beforeFiles = Map.of("bank.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("bank.py", afterPythonCode);

        assertRenameAttributeRefactoringDetected(beforeFiles, afterFiles,
                "_balance", "balance", "BankAccount");
    }

    @Test
    void detectsRenameAttribute_ImprovingNaming() throws Exception {
        String beforePythonCode = """
            class Rectangle:
                def __init__(self, width, height):
                    self.w = width
                    self.h = height
                
                def area(self):
                    return self.w * self.h
                
                def perimeter(self):
                    return 2 * (self.w + self.h)
            """;

        String afterPythonCode = """
            class Rectangle:
                def __init__(self, width, height):
                    self.width = width
                    self.height = height
                
                def area(self):
                    return self.width * self.height
                
                def perimeter(self):
                    return 2 * (self.width + self.height)
            """;

        Map<String, String> beforeFiles = Map.of("rectangle.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("rectangle.py", afterPythonCode);

        assertRenameAttributeRefactoringDetected(beforeFiles, afterFiles,
                "w", "width", "Rectangle");
    }

    public static void assertRenameAttributeRefactoringDetected(
            Map<String, String> beforeFiles,
            Map<String, String> afterFiles,
            String oldAttributeName,
            String newAttributeName,
            String className
    ) throws Exception {
        UMLModelAdapter beforeAdapter = new UMLModelAdapter(beforeFiles);
        UMLModelAdapter afterAdapter = new UMLModelAdapter(afterFiles);

        UMLModel beforeUML = beforeAdapter.getUMLModel();
        UMLModel afterUML = afterAdapter.getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);
        List<Refactoring> refactorings = diff.getRefactorings();

        System.out.println("\n=== RENAME ATTRIBUTE TEST: " + oldAttributeName + " -> " + newAttributeName + " ===");
        System.out.println("Old attribute: " + oldAttributeName);
        System.out.println("New attribute: " + newAttributeName);
        System.out.println("Class: " + className);
        System.out.println("Total refactorings detected: " + refactorings.size());

        boolean renameAttributeFound = refactorings.stream()
                .anyMatch(r -> RefactoringType.RENAME_ATTRIBUTE.equals(r.getRefactoringType()) &&
                        r.toString().contains(oldAttributeName) &&
                        r.toString().contains(newAttributeName) &&
                        r.toString().contains(className));

        if (!renameAttributeFound) {
            System.out.println("Available refactorings:");
            refactorings.forEach(r -> System.out.println("  " + r.getRefactoringType() + ": " + r.toString()));

            fail("Expected rename attribute refactoring from '" + oldAttributeName +
                    "' to '" + newAttributeName + "' in class '" + className + "' was not detected");
        }

        assertTrue(renameAttributeFound, "Expected Rename Attribute refactoring to be detected");
    }
}