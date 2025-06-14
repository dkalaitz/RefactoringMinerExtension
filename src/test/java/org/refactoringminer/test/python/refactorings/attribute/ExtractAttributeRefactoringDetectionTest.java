package org.refactoringminer.test.python.refactorings.attribute;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.refactoringminer.test.python.refactorings.DiffLogger.logExtractAttributeSpecific;
import static org.refactoringminer.test.python.refactorings.DiffLogger.logFullDiffAnalysis;

@Isolated
public class ExtractAttributeRefactoringDetectionTest {

    @Test
    void detectsExtractAttribute_SimpleConstant() throws Exception {
        String beforePythonCode = """
        class Calculator:
            def add_tax(self, amount):
                tax_rate = 0.10
                return amount * tax_rate
            
            def calculate_tax(self, price):
                tax_rate = 0.10
                return price * tax_rate
        """;

        String afterPythonCode = """
        class Calculator:
            def __init__(self):
                self.tax_rate = 0.10
            
            def add_tax(self, amount):
                return amount * self.tax_rate
            
            def calculate_tax(self, price):
                return price * self.tax_rate
        """;

        Map<String, String> beforeFiles = Map.of("calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("calculator.py", afterPythonCode);

        assertExtractAttributeRefactoringDetected(beforeFiles, afterFiles,
                "tax_rate", "0.10", "Calculator");
    }

    @Test
    void detectsExtractAttribute_ConstantToClassAttribute() throws Exception {
        String beforePythonCode = """
            class Circle:
                def __init__(self, radius):
                    self.radius = radius
                
                def calculate_area(self):
                    pi = 3.14159
                    return pi * self.radius * self.radius
                
                def calculate_circumference(self):
                    pi = 3.14159
                    return 2 * pi * self.radius
            """;

        String afterPythonCode = """
            class Circle:
                def __init__(self, radius):
                    self.radius = radius
                    self.pi = 3.14159
                
                def calculate_area(self):
                    return self.pi * self.radius * self.radius
                
                def calculate_circumference(self):
                    return 2 * self.pi * self.radius
            """;

        Map<String, String> beforeFiles = Map.of("circle.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("circle.py", afterPythonCode);

        assertExtractAttributeRefactoringDetected(beforeFiles, afterFiles,
                "pi", "3.14159", "Circle");
    }

    @Test
    void detectsExtractAttribute_ConfigurationValue() throws Exception {
        String beforePythonCode = """
            class DatabaseConnection:
                def __init__(self, host):
                    self.host = host
                
                def connect(self):
                    timeout = 30
                    return self.establish_connection(timeout)
                
                def reconnect(self):
                    timeout = 30
                    return self.establish_connection(timeout)
            """;

        String afterPythonCode = """
            class DatabaseConnection:
                def __init__(self, host):
                    self.host = host
                    self.timeout = 30
                
                def connect(self):
                    return self.establish_connection(self.timeout)
                
                def reconnect(self):
                    return self.establish_connection(self.timeout)
            """;

        Map<String, String> beforeFiles = Map.of("database.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("database.py", afterPythonCode);

        assertExtractAttributeRefactoringDetected(beforeFiles, afterFiles,
                "timeout", "30", "DatabaseConnection");
    }

    public static void assertExtractAttributeRefactoringDetected(
            Map<String, String> beforeFiles,
            Map<String, String> afterFiles,
            String attributeName,
            String attributeValue,
            String className
    ) throws Exception {
        UMLModelAdapter beforeAdapter = new UMLModelAdapter(beforeFiles);
        UMLModelAdapter afterAdapter = new UMLModelAdapter(afterFiles);

        UMLModel beforeUML = beforeAdapter.getUMLModel();
        UMLModel afterUML = afterAdapter.getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);
        List<Refactoring> refactorings = diff.getRefactorings();
        logFullDiffAnalysis(beforeUML, afterUML, diff, "Extract Attribute Refactoring Analysis");
        logExtractAttributeSpecific(diff, attributeName, attributeValue, className);
        System.out.println("\n=== EXTRACT ATTRIBUTE TEST: " + attributeName + " ===");
        System.out.println("Attribute: " + attributeName + " = " + attributeValue);
        System.out.println("Class: " + className);
        System.out.println("Total refactorings detected: " + refactorings.size());

        // Look for ExtractAttributeRefactoring
        boolean extractAttributeFound = refactorings.stream()
                .filter(r -> RefactoringType.EXTRACT_ATTRIBUTE.equals(r.getRefactoringType()))
                .anyMatch(refactoring -> refactoring.getRefactoringType() == RefactoringType.EXTRACT_ATTRIBUTE);

        assertTrue(extractAttributeFound, "Expected Extract Attribute refactoring to be detected");
    }

}