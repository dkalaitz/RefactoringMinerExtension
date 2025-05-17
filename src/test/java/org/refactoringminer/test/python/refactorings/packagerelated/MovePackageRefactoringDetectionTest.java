package org.refactoringminer.test.python.refactorings.packagerelated;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Map;

import static org.refactoringminer.test.python.refactorings.util.RefactoringAssertUtils.assertMovePackageRefactoringDetected;

@Isolated
public class MovePackageRefactoringDetectionTest {

    @Test
    void detectsMovePackage() throws Exception {
        // BEFORE: my_module.py in oldpkg
        String beforePythonCode = """
            # src/oldpkg/my_module.py
            class A:
                pass
            """;
        // AFTER: my_module.py in newpkg
        String afterPythonCode = """
            # src/newpkg/my_module.py
            class A:
                pass
            """;


        Map<String, String> beforeFiles = Map.of("src/oldpkg/A.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("src/newpkg/A.py", afterPythonCode);

        assertMovePackageRefactoringDetected(beforeFiles, afterFiles, "oldpkg", "newpkg");
    }

}
