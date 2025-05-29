package org.refactoringminer.test.python.refactorings;

import antlr.ast.visitor.LangVisitor;
import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.decomposition.*;
import gr.uom.java.xmi.decomposition.replacement.Replacement;
import gr.uom.java.xmi.diff.UMLAbstractClassDiff;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.refactoringminer.test.python.refactorings.util.RefactoringAssertUtils.assertInlineMethodRefactoringDetected;

@Isolated
public class InlineMethodRefactoringDetectionTest {

    @Test
    void detectsInlineMethodWithExpression() throws Exception {
        String beforePythonCode =
                "class Calculator:\n" +
                        "    def add(self, a, b):\n" +
                        "        result = sum_impl(a, b)\n" +
                        "        return result\n" +
                        "\n" +
                        "    def sum_impl(self, a, b):\n" +
                        "        return a + b\n";

        String afterPythonCode =
                "class Calculator:\n" +
                        "    def add(self, a, b):\n" +
                        "        result = a + b\n" +
                        "        return result\n";

        Map<String, String> beforeFiles = Map.of("tests/calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/calculator.py", afterPythonCode);

        assertInlineMethodRefactoringDetected(beforeFiles, afterFiles, "add", "sum_impl");
    }

    @Test
    void detectsInlineMethodWithSimpleReturn() throws Exception {
        String beforePythonCode = """
        class MathUtils:
            def calculate(self, x, y):
                doubled = self.double_value(x)
                return doubled + y
            
            def double_value(self, value):
                return value * 2
        """;

        String afterPythonCode = """
        class MathUtils:
            def calculate(self, x, y):
                doubled = x * 2
                return doubled + y
        """;

        Map<String, String> beforeFiles = Map.of("tests/math_utils.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/math_utils.py", afterPythonCode);

        assertInlineMethodRefactoringDetected(beforeFiles, afterFiles, "calculate", "double_value");
    }

//    @Test
//    void detectsInlineMethodAtTopLevel() throws Exception {
//        String beforePythonCode = """
//        def calculate_total(price, tax_rate):
//            tax = compute_tax(price, tax_rate)
//            return price + tax
//
//        def compute_tax(price, rate):
//            return price * rate
//        """;
//
//        String afterPythonCode = """
//        def calculate_total(price, tax_rate):
//            tax = price * tax_rate
//            return price + tax
//        """;
//
//        Map<String, String> beforeFiles = Map.of("tests/billing.py", beforePythonCode);
//        Map<String, String> afterFiles = Map.of("tests/billing.py", afterPythonCode);
//
//        assertInlineMethodRefactoringDetected(beforeFiles, afterFiles, "calculate_total", "compute_tax");
//    }

}