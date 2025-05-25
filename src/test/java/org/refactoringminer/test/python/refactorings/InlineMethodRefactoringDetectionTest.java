package org.refactoringminer.test.python.refactorings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Map;

import static org.refactoringminer.test.python.refactorings.util.RefactoringAssertUtils.assertInlineMethodRefactoringDetected;

@Isolated
public class InlineMethodRefactoringDetectionTest {

    @Test
    void detectsInlineMethodWithExpression() throws Exception {
        String beforePythonCode =
                "class Calculator:\n" +
                        "    def add(self, a, b):\n" +
                        "        result = sum_impl(a, b)\n" +
                        "        return result\n" +
                        "\n" +
                        "    def sum_impl(self, a, b):\n" +
                        "        return a + b\n";

        String afterPythonCode =
                "class Calculator:\n" +
                        "    def add(self, a, b):\n" +
                        "        result = a + b\n" +
                        "        return result\n";

        Map<String, String> beforeFiles = Map.of("tests/calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/calculator.py", afterPythonCode);

        assertInlineMethodRefactoringDetected(beforeFiles, afterFiles, "add", "sum_impl");
    }
}
