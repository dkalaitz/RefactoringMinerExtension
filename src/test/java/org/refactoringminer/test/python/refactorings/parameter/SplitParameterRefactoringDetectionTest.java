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