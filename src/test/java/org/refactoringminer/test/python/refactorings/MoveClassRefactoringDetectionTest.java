package org.refactoringminer.test.python.refactorings;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.MoveClassRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Isolated
public class MoveClassRefactoringDetectionTest {

    @Test
    void detectsClassMove_Utils_FromHelperToCommon() throws Exception {
        // BEFORE: Utils class in helper.py
        String beforePythonCode1 = """
            class Utils:
                def greet(self, name):
                    print("Hello", name)
            """;

        String beforePythonCode2 = """
            class Other:
                pass
            """;

        // AFTER: Utils class is now in common.py
        String afterPythonCode1 = """
            class Other:
                pass
            """;

        String afterPythonCode2 = """
            class Utils:
                def greet(self, name):
                    print("Hello", name)
            """;

        Map<String, String> beforeFiles = Map.of(
                "src/helper/helper.py", beforePythonCode1,    // ✅ Now: src/tests/helper.py
                "src/tests/other.py", beforePythonCode2
        );

        Map<String, String> afterFiles = Map.of(
                "src/tests/other.py", afterPythonCode1,
                "src/common/common.py", afterPythonCode2      // ✅ Now: src/tests/common.py
        );


        assertMoveClassRefactoringDetected(
                beforeFiles,
                afterFiles,
                "helper.Utils",
                "common.Utils"
        );
    }

    private void assertMoveClassRefactoringDetected(
            Map<String, String> beforeFiles,
            Map<String, String> afterFiles,
            String originalClassQualifiedName,
            String movedClassQualifiedName
    ) throws Exception {
        UMLModel beforeUML = new UMLModelAdapter(beforeFiles).getUMLModel();
        UMLModel afterUML = new UMLModelAdapter(afterFiles).getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);

        System.out.println("=== BEFORE UML CLASSES ===");
        beforeUML.getClassList().forEach(cls -> {
            System.out.println("Class non-qualified: '" + cls.getNonQualifiedName() + "'");
            System.out.println("Class qualified (getName): '" + cls.getName() + "'");
            System.out.println("  Package: '" + cls.getPackageName() + "'");
            System.out.println("  Source file: '" + cls.getSourceFile() + "'");
            System.out.println("---");
        });

        System.out.println("=== AFTER UML CLASSES ===");
        afterUML.getClassList().forEach(cls -> {
            System.out.println("Class non-qualified: '" + cls.getNonQualifiedName() + "'");
            System.out.println("Class qualified (getName): '" + cls.getName() + "'");
            System.out.println("  Package: '" + cls.getPackageName() + "'");
            System.out.println("  Source file: '" + cls.getSourceFile() + "'");
            System.out.println("---");
        });


        boolean moveClassDetected = diff.getRefactorings().stream()
                .anyMatch(ref -> ref instanceof MoveClassRefactoring moveRef &&
                        moveRef.getOriginalClassName().equals(originalClassQualifiedName) &&
                        moveRef.getMovedClassName().equals(movedClassQualifiedName));

        System.out.println("Refactorings size: " + diff.getRefactorings().size() + "\n");
        diff.getRefactorings().forEach(ref -> System.out.println(ref.getName()));
        assertTrue(
                moveClassDetected,
                String.format(
                        "Expected a MoveClassRefactoring of class '%s' moved to '%s'",
                        originalClassQualifiedName,
                        movedClassQualifiedName
                )
        );
    }
}