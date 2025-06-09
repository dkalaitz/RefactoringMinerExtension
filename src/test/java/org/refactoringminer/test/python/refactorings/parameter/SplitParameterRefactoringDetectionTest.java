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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Isolated
public class SplitParameterRefactoringDetectionTest {

    @Test
    void detectsSplitParameter_FullNameToFirstLastName() throws Exception {
        String beforePythonCode = """
            class UserService:
                def create_user(self, full_name, email):
                    parts = full_name.split(' ', 1)
                    first_name = parts[0]
                    last_name = parts[1] if len(parts) > 1 else ""
                    return User(first_name, last_name, email)
                
                def update_user(self, user_id, full_name):
                    parts = full_name.split(' ', 1)
                    first_name = parts[0]
                    last_name = parts[1] if len(parts) > 1 else ""
                    return self.repository.update(user_id, first_name, last_name)
            """;

        String afterPythonCode = """
            class UserService:
                def create_user(self, first_name, last_name, email):
                    return User(first_name, last_name, email)
                
                def update_user(self, user_id, first_name, last_name):
                    return self.repository.update(user_id, first_name, last_name)
            """;

        Map<String, String> beforeFiles = Map.of("user_service.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("user_service.py", afterPythonCode);

        assertSplitParameterRefactoringDetected(beforeFiles, afterFiles,
                "full_name", Set.of("first_name", "last_name"), "create_user", "UserService");
    }

    @Test
    void detectsSplitParameter_ConfigObjectToSeparateParameters() throws Exception {
        String beforePythonCode = """
            def setup_database(db_config):
                host = db_config.get('host', 'localhost')
                port = db_config.get('port', 5432)
                username = db_config.get('username', 'user')
                password = db_config.get('password', 'pass')
                database_name = db_config.get('database', 'mydb')
                
                return DatabaseConnection(host, port, username, password, database_name)
            """;

        String afterPythonCode = """
            def setup_database(host, port, username, password, database_name):
                return DatabaseConnection(host, port, username, password, database_name)
            """;

        Map<String, String> beforeFiles = Map.of("database.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("database.py", afterPythonCode);

        assertSplitParameterRefactoringDetected(beforeFiles, afterFiles,
                "db_config", Set.of("host", "port", "username", "password", "database_name"), "setup_database", "");
    }

    @Test
    void detectsSplitParameter_PositionToCoordinates() throws Exception {
        String beforePythonCode = """
            class GeometryService:
                def create_rectangle(self, position, dimensions):
                    x = position[0]
                    y = position[1]
                    width = dimensions[0]
                    height = dimensions[1]
                    return Rectangle(x, y, width, height)
                
                def move_point(self, point, position):
                    new_x = position[0]
                    new_y = position[1]
                    point.x = new_x
                    point.y = new_y
            """;

        String afterPythonCode = """
            class GeometryService:
                def create_rectangle(self, x, y, width, height):
                    return Rectangle(x, y, width, height)
                
                def move_point(self, point, x, y):
                    point.x = x
                    point.y = y
            """;

        Map<String, String> beforeFiles = Map.of("geometry.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("geometry.py", afterPythonCode);

        assertSplitParameterRefactoringDetected(beforeFiles, afterFiles,
                "position", Set.of("x", "y"), "move_point", "GeometryService");
    }

    @Test
    void detectsSplitParameter_DateTimeToComponents() throws Exception {
        String beforePythonCode = """
            class EventService:
                def create_event(self, title, event_datetime):
                    year = event_datetime.year
                    month = event_datetime.month
                    day = event_datetime.day
                    hour = event_datetime.hour
                    minute = event_datetime.minute
                    
                    return Event(title, year, month, day, hour, minute)
            """;

        String afterPythonCode = """
            class EventService:
                def create_event(self, title, year, month, day, hour, minute):
                    return Event(title, year, month, day, hour, minute)
            """;

        Map<String, String> beforeFiles = Map.of("events.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("events.py", afterPythonCode);

        assertSplitParameterRefactoringDetected(beforeFiles, afterFiles,
                "event_datetime", Set.of("year", "month", "day", "hour", "minute"), "create_event", "EventService");
    }

    public static void assertSplitParameterRefactoringDetected(
            Map<String, String> beforeFiles,
            Map<String, String> afterFiles,
            String originalParameterName,
            Set<String> splitParameterNames,
            String methodName,
            String className
    ) throws Exception {
        UMLModelAdapter beforeAdapter = new UMLModelAdapter(beforeFiles);
        UMLModelAdapter afterAdapter = new UMLModelAdapter(afterFiles);

        UMLModel beforeUML = beforeAdapter.getUMLModel();
        UMLModel afterUML = afterAdapter.getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);
        List<Refactoring> refactorings = diff.getRefactorings();

        System.out.println("\n=== SPLIT PARAMETER TEST: " + originalParameterName + " -> " + splitParameterNames + " ===");
        System.out.println("Original parameter: " + originalParameterName);
        System.out.println("Split parameters: " + splitParameterNames);
        System.out.println("Method: " + methodName + (className.isEmpty() ? " (module level)" : " in class " + className));
        System.out.println("Total refactorings detected: " + refactorings.size());

        boolean splitParameterFound = refactorings.stream()
                .anyMatch(r -> RefactoringType.SPLIT_PARAMETER.equals(r.getRefactoringType()) &&
                        r.toString().contains(originalParameterName) &&
                        r.toString().contains(methodName));

        if (!splitParameterFound) {
            System.out.println("Available refactorings:");
            refactorings.forEach(r -> System.out.println("  " + r.getRefactoringType() + ": " + r.toString()));

            fail("Expected split parameter refactoring from parameter '" + originalParameterName +
                    "' to parameters " + splitParameterNames + " in method '" + methodName + "' was not detected");
        }

        assertTrue(splitParameterFound, "Expected Split Parameter refactoring to be detected");
    }
}