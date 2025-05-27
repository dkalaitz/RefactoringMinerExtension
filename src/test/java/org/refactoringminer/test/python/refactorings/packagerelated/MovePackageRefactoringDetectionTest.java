package org.refactoringminer.test.python.refactorings.packagerelated;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.RenamePackageRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Map;

import static org.refactoringminer.test.python.refactorings.util.RefactoringAssertUtils.assertMovePackageRefactoringDetected;

@Isolated
public class MovePackageRefactoringDetectionTest {

    @Test
    void detectsMovePackage() throws Exception {

        String beforePythonCode = """
            class A:
                pass
            """;
        String afterPythonCode = """
            class A:
                pass
            """;

        // Create hierarchical package paths (with dots) to trigger MOVE_PACKAGE detection
        Map<String, String> beforeFiles = Map.of(
                "src/com/example/oldpkg/A.py", beforePythonCode,
                "src/com/example/oldpkg/__init__.py", "# Package initialization"
        );

        Map<String, String> afterFiles = Map.of(
                "src/org/example/newpkg/A.py", afterPythonCode,
                "src/org/example/newpkg/__init__.py", "# Package initialization"
        );

        assertMovePackageRefactoringDetected(beforeFiles, afterFiles, "com.example.oldpkg", "org.example.newpkg");
    }

}
