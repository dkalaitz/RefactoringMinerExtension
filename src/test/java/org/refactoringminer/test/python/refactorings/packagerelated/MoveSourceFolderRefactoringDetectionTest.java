package org.refactoringminer.test.python.refactorings.packagerelated;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.refactoringminer.test.python.refactorings.util.RefactoringAssertUtils.assertMoveSourceFolderRefactoringDetected;

class MoveSourceFolderRefactoringDetectionTest {

    @Test
    void detectsMoveSourceFolder() throws Exception {
        // BEFORE: Everything under "src_old"
        String beforePythonCode = """
            # src_old/mypkg/my_module.py
            class A:
                pass
            """;

        Map<String, String> beforeFiles = Map.of("src_old/mypkg/my_module.py", beforePythonCode);

        // AFTER: Everything under "src_new"
        String afterPythonCode = """
            # src_new/mypkg/my_module.py
            class A:
                pass
            """;

        Map<String, String> afterFiles = Map.of("src_new/mypkg/my_module.py", afterPythonCode);

        assertMoveSourceFolderRefactoringDetected(beforeFiles, afterFiles, "src_old", "src_new");
    }
}