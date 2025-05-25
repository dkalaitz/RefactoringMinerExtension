package org.refactoringminer.test.python.refactorings.packagerelated;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static antlr.umladapter.UMLAdapterUtil.*;
import static org.refactoringminer.test.python.refactorings.util.RefactoringAssertUtils.assertRenamePackageRefactoringDetected;

class RenamePackageRefactoringDetectionTest {

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