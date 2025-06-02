package org.refactoringminer.test.python.refactorings.attribute;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.MoveAttributeRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Isolated
public class MoveAttributeRefactoringDetectionTest {

    @Test
    void detectsMoveAttributeBetweenClasses() throws Exception {
        String beforePythonCode = """
        class User:
            def __init__(self):
                self.name = "John"
                self.email = "john@example.com"

        class Profile:
            def __init__(self):
                self.bio = "Software developer"
        """;

        String afterPythonCode = """
        class User:
            def __init__(self):
                self.name = "John"

        class Profile:
            def __init__(self):
                self.bio = "Software developer"
                self.email = "john@example.com"
        """;

        Map<String, String> beforeFiles = Map.of("tests/user.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/user.py", afterPythonCode);

        assertMoveAttributeRefactoringDetected(beforeFiles, afterFiles, "User", "Profile", "email");
    }

    @Test
    void detectsMoveAttributeFromParentToChild() throws Exception {
        String beforePythonCode = """
        class Vehicle:
            def __init__(self):
                self.wheels = 4
                self.engine = "V6"

        class Car(Vehicle):
            def __init__(self):
                super().__init__()
                self.doors = 4
        """;

        String afterPythonCode = """
        class Vehicle:
            def __init__(self):
                self.wheels = 4

        class Car(Vehicle):
            def __init__(self):
                super().__init__()
                self.doors = 4
                self.engine = "V6"
        """;

        Map<String, String> beforeFiles = Map.of("tests/vehicle.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/vehicle.py", afterPythonCode);

        assertMoveAttributeRefactoringDetected(beforeFiles, afterFiles, "Vehicle", "Car", "engine");
    }

    @Test
    void detectsMoveAttributeFromChildToParent() throws Exception {
        String beforePythonCode = """
        class Shape:
            def __init__(self):
                self.x = 0
                self.y = 0

        class Circle(Shape):
            def __init__(self):
                super().__init__()
                self.radius = 5
                self.color = "red"

        class Rectangle(Shape):
            def __init__(self):
                super().__init__()
                self.width = 10
                self.height = 8
                self.color = "blue"
        """;

        String afterPythonCode = """
        class Shape:
            def __init__(self):
                self.x = 0
                self.y = 0
                self.color = "black"

        class Circle(Shape):
            def __init__(self):
                super().__init__()
                self.radius = 5

        class Rectangle(Shape):
            def __init__(self):
                super().__init__()
                self.width = 10
                self.height = 8
        """;

        Map<String, String> beforeFiles = Map.of("tests/shape.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/shape.py", afterPythonCode);

        assertMoveAttributeRefactoringDetected(beforeFiles, afterFiles, "Circle", "Shape", "color");
    }

    @Test
    void detectsMoveAttributeBetweenSiblingClasses() throws Exception {
        String beforePythonCode = """
        class Employee:
            def __init__(self):
                self.name = "Alice"
                self.id = 12345

        class Manager:
            def __init__(self):
                self.department = "Engineering"
                self.team_size = 10
        """;

        String afterPythonCode = """
        class Employee:
            def __init__(self):
                self.name = "Alice"
                self.department = "Engineering"

        class Manager:
            def __init__(self):
                self.id = 12345
                self.team_size = 10
        """;

        Map<String, String> beforeFiles = Map.of("tests/employee.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/employee.py", afterPythonCode);

        assertMoveAttributeRefactoringDetected(beforeFiles, afterFiles, "Employee", "Manager", "id");
    }

    @Test
    void detectsMoveAttributeWithComplexInitializer() throws Exception {
        String beforePythonCode = """
        class Database:
            def __init__(self):
                self.connection = self.create_connection()
                self.tables = []

        class Cache:
            def __init__(self):
                self.size = 1000
        """;

        String afterPythonCode = """
        class Database:
            def __init__(self):
                self.tables = []

        class Cache:
            def __init__(self):
                self.size = 1000
                self.connection = self.create_connection()
        """;

        Map<String, String> beforeFiles = Map.of("tests/database.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/database.py", afterPythonCode);

        assertMoveAttributeRefactoringDetected(beforeFiles, afterFiles, "Database", "Cache", "connection");
    }

    public static void assertMoveAttributeRefactoringDetected(Map<String, String> beforeFiles,
                                                              Map<String, String> afterFiles,
                                                              String sourceClassName,
                                                              String targetClassName,
                                                              String attributeName) throws Exception {
        UMLModel beforeUML = new UMLModelAdapter(beforeFiles).getUMLModel();
        UMLModel afterUML = new UMLModelAdapter(afterFiles).getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);

        boolean moveAttributeDetected = diff.getRefactorings().stream()
                .anyMatch(ref -> {
                    if (ref instanceof MoveAttributeRefactoring moveAttribute) {
                        UMLAttribute originalAttribute = moveAttribute.getOriginalAttribute();
                        UMLAttribute movedAttribute = moveAttribute.getMovedAttribute();

                        return originalAttribute.getName().equals(attributeName) &&
                                movedAttribute.getName().equals(attributeName) &&
                                moveAttribute.getSourceClassName().equals(sourceClassName) &&
                                moveAttribute.getTargetClassName().equals(targetClassName);
                    }
                    return false;
                });

        assertTrue(moveAttributeDetected,
                String.format("Expected Move Attribute refactoring: attribute '%s' from class '%s' to class '%s'",
                        attributeName, sourceClassName, targetClassName));
    }

}
