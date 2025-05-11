package org.refactoringminer.test.python.refactorings.attribute;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.refactoringminer.test.python.refactorings.util.RefactoringAssertUtils.assertPullUpAttributeRefactoringDetected;

public class PullUpAttributeRefactoringDetectionTest {

    @Test
    void detectsPullUpAttribute() throws Exception {
        String beforePythonCode = """
        class Animal:
            pass

        class Dog(Animal):
            def __init__(self):
                self.legs = 4

        class Cat(Animal):
            def __init__(self):
                self.legs = 4
        """;
        String afterPythonCode = """
        class Animal:
            def __init__(self):
                self.legs = 4

        class Dog(Animal):
            pass

        class Cat(Animal):
            pass
        """;

        Map<String, String> beforeFiles = Map.of("tests/before/animal.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/after/animal.py", afterPythonCode);

        assertPullUpAttributeRefactoringDetected(beforeFiles, afterFiles, "Dog", "Animal", "legs");
    }
}
