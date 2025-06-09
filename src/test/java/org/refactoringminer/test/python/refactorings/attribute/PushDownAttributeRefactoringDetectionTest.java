package org.refactoringminer.test.python.refactorings.attribute;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.refactoringminer.utils.RefactoringAssertUtils.assertPushDownAttributeRefactoringDetected;

public class PushDownAttributeRefactoringDetectionTest {

    @Test
    void detectsPushDownAttribute() throws Exception {

        String beforePythonCode = """
class Animal:
    def __init__(self):
        self.tail = True

class Dog(Animal):
    pass

class Cat(Animal):
    pass
""";
        String afterPythonCode = """
class Animal:
    def __init__(self):
        pass

class Dog(Animal):
    def __init__(self):
        self.tail = True

class Cat(Animal):
    def __init__(self):
        pass
""";

        Map<String, String> beforeFiles = Map.of("tests/animal.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/animal.py", afterPythonCode);

        assertPushDownAttributeRefactoringDetected(beforeFiles, afterFiles, "Animal", "Dog", "tail");
    }
}
