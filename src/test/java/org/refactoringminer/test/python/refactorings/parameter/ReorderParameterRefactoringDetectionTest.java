package org.refactoringminer.test.python.refactorings.parameter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Map;

import static org.refactoringminer.utils.RefactoringAssertUtils.assertReorderParameterRefactoringDetected;

@Isolated
public class ReorderParameterRefactoringDetectionTest {

    @Test
    void detectsSimpleReorderParameter() throws Exception {
        String beforePythonCode = """
            class MathOps:
                def add(self, x, y):
                    return x + y
            """;
        String afterPythonCode = """
            class MathOps:
                def add(self, y, x):
                    return x + y
            """;

        Map<String, String> beforeFiles = Map.of("tests/mathops.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/mathops.py", afterPythonCode);

        assertReorderParameterRefactoringDetected(
                beforeFiles,
                afterFiles,
                "MathOps",      // The class name in your test
                "add",          // The method name in your test
                new String[]{"x : Object", "y : Object"}, // Parameter names and types as detected
                new String[]{"y : Object", "x : Object"}
        );

    }

}
