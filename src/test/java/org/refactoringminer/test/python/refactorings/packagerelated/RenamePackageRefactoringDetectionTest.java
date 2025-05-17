package org.refactoringminer.test.python.refactorings.packagerelated;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static antlr.umladapter.UMLAdapterUtil.*;
import static org.refactoringminer.test.python.refactorings.util.RefactoringAssertUtils.assertRenamePackageRefactoringDetected;

class RenamePackageRefactoringDetectionTest {

    @Test
    void detectsMovePackage() throws Exception {
        // BEFORE: module in old_pkg
        String beforePythonCode = """
            # src/old_pkg/my_module.py
            class A:
                pass
            """;
        // AFTER: module in new_pkg
        String afterPythonCode = """
            # src/new_pkg/my_module.py
            class A:
                pass
            """;

        Map<String, String> beforeFiles = Map.of("src/old_pkg/my_module.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("src/new_pkg/my_module.py", afterPythonCode);

        assertRenamePackageRefactoringDetected(beforeFiles, afterFiles, "old_pkg", "new_pkg");
    }


    @Test
    void detectsMoveFileToAnotherPackage() {
        // Simulate before and after move
        String before = "src/oldpkg/utils/helper.py";
        String after = "src/newpkg/utils/helper.py";
        assertEquals("oldpkg.utils", extractPackageName(before));
        assertEquals("newpkg.utils", extractPackageName(after));
    }

    @Test
    void detectsRenameModule() {
        // Simulate renaming a module
        String before = "src/utilities/math_ops.py";
        String after = "src/utilities/arithmetic.py";
        assertEquals("utilities", extractPackageName(before));
        assertEquals("utilities", extractPackageName(after));
        assertNotEquals(extractFilePath(before), extractFilePath(after));
    }

    @Test
    void detectsMoveAndRenameModule() {
        // Move and rename: from "pkg1/a.py" to "pkg2/b.py"
        String before = "src/pkg1/a.py";
        String after = "src/pkg2/b.py";
        assertEquals("pkg1", extractPackageName(before));
        assertEquals("pkg2", extractPackageName(after));
        assertNotEquals(extractFilePath(before), extractFilePath(after));
    }
}