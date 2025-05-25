package org.refactoringminer.test.python.refactorings.parameter;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Map;

@Isolated
public class AddParameterRefactoringDetectionTest {

    @Test
    void detectsAddParameter() throws Exception {
        String beforePythonCode = """
            class Calculator:
                def multiply(self, x):
                    return x * x
            """;
        String afterPythonCode = """
            class Calculator:
                def multiply(self, x, y):
                    return x * y
            """;

        Map<String, String> beforeFiles = Map.of("tests/calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/calculator.py", afterPythonCode);

        UMLModel beforeUML = new UMLModelAdapter(beforeFiles).getUMLModel();
        UMLModel afterUML = new UMLModelAdapter(afterFiles).getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);

        boolean addParamDetected = diff.getRefactorings().stream()
                .anyMatch(ref -> ref.getName().startsWith("Add Parameter") &&
                        ref.toString().contains("multiply")); // You can make this stricter

        System.out.println("Detected refactorings:");
        diff.getRefactorings().forEach(System.out::println);

        assertTrue(addParamDetected, "Expected add parameter refactoring in 'multiply'");
    }
}