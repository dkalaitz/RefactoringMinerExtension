package org.refactoringminer.test.python.refactorings.method;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Map;

import static org.refactoringminer.utils.RefactoringAssertUtils.assertInlineMethodRefactoringDetected;

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

    @Test
    void detectsInlineMethodWithSimpleReturn() throws Exception {
        String beforePythonCode = """
        class MathUtils:
            def calculate(self, x, y):
                doubled = self.double_value(x)
                return doubled + y
            
            def double_value(self, value):
                return value * 2
        """;

        String afterPythonCode = """
        class MathUtils:
            def calculate(self, x, y):
                doubled = x * 2
                return doubled + y
        """;

        Map<String, String> beforeFiles = Map.of("tests/math_utils.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/math_utils.py", afterPythonCode);

        assertInlineMethodRefactoringDetected(beforeFiles, afterFiles, "calculate", "double_value");
    }

}