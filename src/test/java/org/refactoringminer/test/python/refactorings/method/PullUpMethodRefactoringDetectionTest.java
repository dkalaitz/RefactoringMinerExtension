package org.refactoringminer.test.python.refactorings.method;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Map;

import static org.refactoringminer.test.python.refactorings.util.RefactoringAssertUtils.assertPullUpMethodRefactoringDetected;

@Isolated
public class PullUpMethodRefactoringDetectionTest {

    @Test
    void detectsPullUpMethodWithMultipleSubclasses() throws Exception {
        String beforePythonCode = """
            class Animal:
                pass

            class Dog(Animal):
                def speak(self):
                    return "Woof"

            class Cat(Animal):
                pass
            """;
        String afterPythonCode = """
            class Animal:
                def speak(self):
                    return "Woof"

            class Dog(Animal):
                pass

            class Cat(Animal):
                pass
            """;

        Map<String, String> beforeFiles = Map.of("tests/before/pullup_multi.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/after/pullup_multi.py", afterPythonCode);

        assertPullUpMethodRefactoringDetected(beforeFiles, afterFiles, "Dog", "Animal", "speak");
    }

}
