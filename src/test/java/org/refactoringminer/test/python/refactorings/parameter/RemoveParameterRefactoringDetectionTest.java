package org.refactoringminer.test.python.refactorings.parameter;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Isolated
public class RemoveParameterRefactoringDetectionTest {

  @Test
  void detectsRemoveParameter() throws Exception {
    String beforePythonCode =
        """
            class Calculator:
                def add(self, x, y):
                    return x + y
            """;
    String afterPythonCode =
        """
            class Calculator:
                def add(self, x):
                    return x
            """;

    Map<String, String> beforeFiles = Map.of("tests/calculator.py", beforePythonCode);
    Map<String, String> afterFiles = Map.of("tests/calculator.py", afterPythonCode);

    UMLModel beforeUML = new UMLModelAdapter(beforeFiles).getUMLModel();
    UMLModel afterUML = new UMLModelAdapter(afterFiles).getUMLModel();

    UMLModelDiff diff = beforeUML.diff(afterUML);

    boolean removeParamDetected =
        diff.getRefactorings().stream()
            .anyMatch(
                ref ->
                    ref.getName().startsWith("Remove Parameter")
                        && ref.toString().contains("add")); // You can make this stricter

    System.out.println("Detected refactorings:");
    diff.getRefactorings().forEach(System.out::println);

    assertTrue(removeParamDetected, "Expected remove parameter refactoring in 'add'");
  }
}