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
                "tests/before/helper.py", beforePythonCode1,
                "tests/before/other.py", beforePythonCode2
        );

        Map<String, String> afterFiles = Map.of(
                "tests/after/other.py", afterPythonCode1,
                "tests/after/common.py", afterPythonCode2
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

        boolean moveClassDetected = diff.getRefactorings().stream()
                .anyMatch(ref -> ref instanceof MoveClassRefactoring moveRef &&
                        moveRef.getOriginalClassName().equals(originalClassQualifiedName) &&
                        moveRef.getMovedClassName().equals(movedClassQualifiedName));

        System.out.println("Refactorings size: " + diff.getRefactorings().size() + "\n");
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