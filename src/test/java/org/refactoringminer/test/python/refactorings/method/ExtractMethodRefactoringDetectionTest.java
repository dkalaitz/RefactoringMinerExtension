package org.refactoringminer.test.python.refactorings.method;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.refactoringminer.utils.RefactoringAssertUtils.*;

@Isolated
public class ExtractMethodRefactoringDetectionTest {

    @Test
    void detectsExtractFunction_SimpleClassPython() throws Exception {
        String beforePythonCode = """
    class Example:
        def foo(self):
            x = 1
            y = 2
            print(x + y)
            print("Done")
    """;

        String afterPythonCode = """
    class Example:
        def foo(self):
            bar()
            print("Done")

        def bar():
            x = 1
            y = 2
            print(x + y)
    """;


        Map<String, String> beforeFiles = Map.of("tests/example.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/example.py", afterPythonCode);

        UMLModel beforeUML = new UMLModelAdapter(beforeFiles).getUMLModel();
        UMLModel afterUML = new UMLModelAdapter(afterFiles).getUMLModel();

        UMLModelDiff umlModelDiff = beforeUML.diff(afterUML);

        System.out.println("Refactorings size: " + umlModelDiff.getRefactorings().size() + "\n");
        System.out.println("Refactoring: " + umlModelDiff.getRefactorings().get(0).getName() + "\n");
        boolean extractDetected = detectExtractMethod(beforeUML, afterUML, "foo", "bar");

        assertTrue(extractDetected, "Expected extract function refactoring from foo to bar");
    }


    @Test
    void detectsExtractFunction() throws Exception {
        String beforePythonCode = """
        class Calculator:
            def operate(self, x, y):
                sum = x + y
                print("Sum:", sum)
                diff = x - y
                print("Difference:", diff)
        """;

        String afterPythonCode = """
        class Calculator:
            def operate(self, x, y):
                self.print_sum(x, y)
                diff = x - y
                print("Difference:", diff)
            
            def print_sum(self, x, y):
                sum = x + y
                print("Sum:", sum)
        """;

        Map<String, String> beforeFiles = Map.of("tests/calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/calculator.py", afterPythonCode);

        UMLModel beforeUML = new UMLModelAdapter(beforeFiles).getUMLModel();
        UMLModel afterUML = new UMLModelAdapter(afterFiles).getUMLModel();

        // Debug output of models
        dumpModels(beforeUML, afterUML);

        // Manual detection of extract method
        boolean extractDetected = detectExtractMethod(beforeUML, afterUML, "operate", "print_sum");

        assertTrue(extractDetected, "Expected extract function refactoring from operate to print_sum");
    }
}
