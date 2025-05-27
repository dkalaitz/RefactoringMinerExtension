package org.refactoringminer.test.python.refactorings.method;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.ChangeReturnTypeRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Isolated
public class ChangeReturnTypeRefactoringDetectionTest {

    @Test
    void detectsMethodReturnTypeChange() throws Exception {
        String beforePythonCode = """
        class TypeExample:
            def calculate_sum(self, a, b) -> int:
                return a + b
        """;

        String afterPythonCode = """
        class TypeExample:
            def calculate_sum(self, a, b) -> float:
                return a + b
        """;

        Map<String, String> beforeFiles = Map.of("tests/example.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/example.py", afterPythonCode);

        assertReturnTypeChangeDetected(beforeFiles, afterFiles,
                "calculate_sum", "int",
                "calculate_sum", "float");
    }

    private void assertReturnTypeChangeDetected(Map<String, String> beforeFiles, Map<String, String> afterFiles,
                                                String beforeMethodName, String beforeReturnType,
                                                String afterMethodName, String afterReturnType) throws Exception {
        UMLModel beforeUML = new UMLModelAdapter(beforeFiles).getUMLModel();
        UMLModel afterUML = new UMLModelAdapter(afterFiles).getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);

        boolean returnTypeChangeDetected = diff.getRefactorings().stream()
                .anyMatch(ref -> {
                    if (ref instanceof ChangeReturnTypeRefactoring returnTypeChange) {
                        UMLOperation originalOperation = returnTypeChange.getOperationBefore();
                        UMLOperation changedOperation = returnTypeChange.getOperationAfter();

                        return originalOperation.getName().equals(beforeMethodName) &&
                                changedOperation.getName().equals(afterMethodName) &&
                                returnTypeChange.getOriginalType().getClassType().equals(beforeReturnType) &&
                                returnTypeChange.getChangedType().getClassType().equals(afterReturnType);
                    }
                    return false;
                });

        assertTrue(returnTypeChangeDetected,
                "Expected a return type change from " + beforeReturnType +
                        " to " + afterReturnType + " for method " + beforeMethodName);
    }

}
