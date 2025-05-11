package org.refactoringminer.test.python.refactorings;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.MoveAttributeRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MoveFieldRefactoringDetectionTest {

    @Test
    void detectsFieldMove_role_FromEmployeeToPermissions() throws Exception {
        String beforePythonCode1 = """
        class Employee:
            def __init__(self, name, role):
                self.name = name
                self.role = role
        """;

        String beforePythonCode2 = """
        class Permissions:
            pass
        """;

        String afterPythonCode1 = """
        class Employee:
            def __init__(self, name):
                self.name = name
        """;

        String afterPythonCode2 = """
        class Permissions:
            def __init__(self, role):
                self.role = role
        """;

        Map<String, String> beforeFiles = Map.of(
                "employee.py", beforePythonCode1,
                "permissions.py", beforePythonCode2
        );
        Map<String, String> afterFiles = Map.of(
                "employee.py", afterPythonCode1,
                "permissions.py", afterPythonCode2
        );

        assertMoveFieldRefactoringDetected(
                beforeFiles,
                afterFiles,
                "Employee",
                "role",
                "Permissions"
        );
    }

    @Test
    void detectsFieldMove_Email_FromUserToContactInfo() throws Exception {
        // BEFORE: 'email' field in User class
        String beforePythonCode1 = """
            class User:
                def __init__(self, name, email):
                    self.name = name
                    self.email = email
            """;

        String beforePythonCode2 = """
            class ContactInfo:
                pass
            """;

        // AFTER: 'email' field is now in ContactInfo class
        String afterPythonCode1 = """
            class User:
                def __init__(self, name):
                    self.name = name
            """;

        String afterPythonCode2 = """
            class ContactInfo:
                def __init__(self, email):
                    self.email = email
            """;

        Map<String, String> beforeFiles = Map.of(
                "tests/before/user.py", beforePythonCode1,
                "tests/before/contact_info.py", beforePythonCode2
        );

        Map<String, String> afterFiles = Map.of(
                "tests/after/user.py", afterPythonCode1,
                "tests/after/contact_info.py", afterPythonCode2
        );

        assertMoveFieldRefactoringDetected(
                beforeFiles,
                afterFiles,
                "User",
                "email",
                "ContactInfo"
        );
    }


    @Test
    void detectsFieldMove_Status_FromOrderToOrderStatusHelper() throws Exception {
        // BEFORE: 'status' field in Order class
        String beforePythonCode1 = """
            class Order:
                def __init__(self, id, status):
                    self.id = id
                    self.status = status
            """;

        String beforePythonCode2 = """
            class OrderStatusHelper:
                pass
            """;

        // AFTER: 'status' field is now in OrderStatusHelper class
        String afterPythonCode1 = """
            class Order:
                def __init__(self, id):
                    self.id = id
            """;

        String afterPythonCode2 = """
            class OrderStatusHelper:
                def __init__(self, status):
                    self.status = status
            """;

        Map<String, String> beforeFiles = Map.of(
                "tests/before/order.py", beforePythonCode1,
                "tests/before/status_helper.py", beforePythonCode2
        );

        Map<String, String> afterFiles = Map.of(
                "tests/after/order.py", afterPythonCode1,
                "tests/after/status_helper.py", afterPythonCode2
        );

        assertMoveFieldRefactoringDetected(
                beforeFiles,
                afterFiles,
                "Order",
                "status",
                "OrderStatusHelper"
        );
    }

    private void assertMoveFieldRefactoringDetected(
            Map<String, String> beforeFiles,
            Map<String, String> afterFiles,
            String sourceClassName,
            String fieldName,
            String targetClassName
    ) throws Exception {
        UMLModel beforeUML = new UMLModelAdapter(beforeFiles).getUMLModel();
        UMLModel afterUML = new UMLModelAdapter(afterFiles).getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);

        boolean moveFieldDetected = diff.getRefactorings().stream()
                .anyMatch(ref -> ref instanceof MoveAttributeRefactoring moveRef &&
                        moveRef.getOriginalAttribute().getName().equals(fieldName) &&
                        moveRef.getOriginalAttribute().getClassName().equals(sourceClassName) &&
                        moveRef.getMovedAttribute().getClassName().equals(targetClassName));

        System.out.println("Refactorings size: " + diff.getRefactorings().size() + "\n");
        System.out.println("Refactoring: " + diff.getRefactorings().get(0).getName() + "\n");
        assertTrue(
                moveFieldDetected,
                String.format(
                        "Expected a MoveAttributeRefactoring of field '%s' from class '%s' to class '%s'",
                        fieldName,
                        sourceClassName,
                        targetClassName
                )
        );
    }
}