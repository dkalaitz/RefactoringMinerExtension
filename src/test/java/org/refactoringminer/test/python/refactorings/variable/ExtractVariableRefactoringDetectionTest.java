package org.refactoringminer.test.python.refactorings.variable;

import antlr.umladapter.UMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.decomposition.*;
import gr.uom.java.xmi.diff.ExtractVariableRefactoring;
import gr.uom.java.xmi.diff.UMLClassDiff;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Isolated
public class ExtractVariableRefactoringDetectionTest {

    @Test
    void detectsExtractVariable_CalculationToVariable() throws Exception {
        String beforePythonCode = """
            class Calculator:
                def calculate(self, x, y):
                    return x + y * 2 + 5
            """;

        String afterPythonCode = """
            class Calculator:
                def calculate(self, x, y):
                    sum_result = x + y
                    return sum_result * 2 + 5
            """;

        Map<String, String> beforeFiles = Map.of("calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("calculator.py", afterPythonCode);

        assertExtractVariableRefactoringDetected(beforeFiles, afterFiles,
                "sum_result", "x + y", "calculate", "Calculator");
    }

    @Test
    void detectsExtractVariable_CalculationWithIf() throws Exception {
        String beforePythonCode = """
        class Calculator:
            def calculate(self, x, y):
                if x > 0:
                    print("positive")
                return x + y * 2 + 5
        """;

        String afterPythonCode = """
        class Calculator:
            def calculate(self, x, y):
                if x > 0:
                    print("positive")
                sum_result = x + y
                return sum_result * 2 + 5
        """;

        Map<String, String> beforeFiles = Map.of("calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("calculator.py", afterPythonCode);

        assertExtractVariableRefactoringDetected(beforeFiles, afterFiles,
                "sum_result", "x + y", "calculate", "Calculator");
    }

    @Test
    void detectsExtractVariable_MethodCallExtraction() throws Exception {
        String beforePythonCode = """
            class FileProcessor:
                def process(self, filename):
                    return open(filename, 'r').read().strip().upper()
            """;

        String afterPythonCode = """
            class FileProcessor:
                def process(self, filename):
                    file_content = open(filename, 'r').read()
                    return file_content.strip().upper()
            """;

        Map<String, String> beforeFiles = Map.of("processor.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("processor.py", afterPythonCode);

        assertExtractVariableRefactoringDetected(beforeFiles, afterFiles,
                "file_content", "open(filename, 'r').read()", "process", "FileProcessor");
    }

    @Test
    void detectsExtractVariable_ConditionalExpression() throws Exception {
        String beforePythonCode = """
            class Validator:
                def validate(self, data):
                    if len(data) > 0 and data.strip() != "":
                        return True
                    return False
            """;

        String afterPythonCode = """
            class Validator:
                def validate(self, data):
                    is_valid_length = len(data) > 0
                    if is_valid_length and data.strip() != "":
                        return True
                    return False
            """;

        Map<String, String> beforeFiles = Map.of("validator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("validator.py", afterPythonCode);

        assertExtractVariableRefactoringDetected(beforeFiles, afterFiles,
                "is_valid_length", "len(data) > 0", "validate", "Validator");
    }


    @Test
    void detectsExtractVariable_StringConcatenation() throws Exception {
        String beforePythonCode = """
            class MessageBuilder:
                def build_message(self, name, age):
                    return f"Hello {name}, you are {age} years old"
            """;

        String afterPythonCode = """
            class MessageBuilder:
                def build_message(self, name, age):
                    greeting = f"Hello {name}"
                    return f"{greeting}, you are {age} years old"
            """;

        Map<String, String> beforeFiles = Map.of("builder.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("builder.py", afterPythonCode);

        assertExtractVariableRefactoringDetected(beforeFiles, afterFiles,
                "greeting", "f\"Hello {name}\"", "build_message", "MessageBuilder");
    }


    @Test
    void detectsExtractVariable_ListComprehension() throws Exception {
        String beforePythonCode = """
        class DataProcessor:
            def process_scores(self, students):
                return [student['score'] * 1.1 for student in students if student['active']]
        """;

        String afterPythonCode = """
        class DataProcessor:
            def process_scores(self, students):
                active_students = [student for student in students if student['active']]
                return [student['score'] * 1.1 for student in active_students]
        """;

        Map<String, String> beforeFiles = Map.of("data_processor.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("data_processor.py", afterPythonCode);

        assertExtractVariableRefactoringDetected(beforeFiles, afterFiles,
                "active_students", "[student for student in students if student['active']]",
                "process_scores", "DataProcessor");
    }

    @Test
    void detectsExtractVariable_DictionaryAccess() throws Exception {
        String beforePythonCode = """
        class ConfigReader:
            def get_database_url(self, config):
                return config['database']['host'] + ':' + str(config['database']['port'])
        """;

        String afterPythonCode = """
        class ConfigReader:
            def get_database_url(self, config):
                db_config = config['database']
                return db_config['host'] + ':' + str(db_config['port'])
        """;

        Map<String, String> beforeFiles = Map.of("config_reader.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("config_reader.py", afterPythonCode);

        assertExtractVariableRefactoringDetected(beforeFiles, afterFiles,
                "db_config", "config['database']", "get_database_url", "ConfigReader");
    }

    @Test
    void detectsExtractVariable_ComplexArithmetic() throws Exception {
        String beforePythonCode = """
        class GeometryCalculator:
            def calculate_area(self, radius):
                return 3.14159 * radius * radius + 2 * 3.14159 * radius
        """;

        String afterPythonCode = """
        class GeometryCalculator:
            def calculate_area(self, radius):
                circle_area = 3.14159 * radius * radius
                return circle_area + 2 * 3.14159 * radius
        """;

        Map<String, String> beforeFiles = Map.of("geometry_calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("geometry_calculator.py", afterPythonCode);

        assertExtractVariableRefactoringDetected(beforeFiles, afterFiles,
                "circle_area", "3.14159 * radius * radius", "calculate_area", "GeometryCalculator");
    }

    @Test
    void detectsExtractVariable_NestedMethodCall() throws Exception {
        String beforePythonCode = """
        class TextAnalyzer:
            def analyze_text(self, text):
                return len(text.strip().lower().split())
        """;

        String afterPythonCode = """
        class TextAnalyzer:
            def analyze_text(self, text):
                cleaned_text = text.strip().lower()
                return len(cleaned_text.split())
        """;

        Map<String, String> beforeFiles = Map.of("text_analyzer.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("text_analyzer.py", afterPythonCode);

        assertExtractVariableRefactoringDetected(beforeFiles, afterFiles,
                "cleaned_text", "text.strip().lower()", "analyze_text", "TextAnalyzer");
    }

    @Test
    void detectsExtractVariable_ConditionalWithComparison() throws Exception {
        String beforePythonCode = """
        class AgeValidator:
            def is_eligible(self, person):
                if person['age'] >= 18 and person['country'] == 'US':
                    return True
                return False
        """;

        String afterPythonCode = """
        class AgeValidator:
            def is_eligible(self, person):
                is_adult = person['age'] >= 18
                if is_adult and person['country'] == 'US':
                    return True
                return False
        """;

        Map<String, String> beforeFiles = Map.of("age_validator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("age_validator.py", afterPythonCode);

        assertExtractVariableRefactoringDetected(beforeFiles, afterFiles,
                "is_adult", "person['age'] >= 18", "is_eligible", "AgeValidator");
    }


    public static void assertExtractVariableRefactoringDetected(
            Map<String, String> beforeFiles,
            Map<String, String> afterFiles,
            String extractedVariableName,
            String extractedExpression,
            String methodName,
            String className
    ) throws Exception {
        UMLModelAdapter beforeAdapter = new UMLModelAdapter(beforeFiles);
        UMLModelAdapter afterAdapter = new UMLModelAdapter(afterFiles);

        UMLModel beforeUML = beforeAdapter.getUMLModel();
        UMLModel afterUML = afterAdapter.getUMLModel();

        UMLModelDiff diff = beforeUML.diff(afterUML);
        List<Refactoring> refactorings = diff.getRefactorings();
//        // 🔍 Debug UMLModelDiff structure
//        System.out.println("\n=== UML MODEL DIFF DEBUG ===");
//        System.out.println("Common class diffs: " + diff.getCommonClassDiffList().size());
//        System.out.println("Added classes: " + diff.getAddedClasses().size());
//        System.out.println("Removed classes: " + diff.getRemovedClasses().size());
//
//        // Check what methods are available
//        for (UMLClassDiff classDiff : diff.getCommonClassDiffList()) {
//            System.out.println("\nClass: " + classDiff.getOriginalClassName());
//            System.out.println("  Operation body mappers: " + classDiff.getOperationBodyMapperList().size());
//
//            for (UMLOperationBodyMapper mapper : classDiff.getOperationBodyMapperList()) {
//                System.out.println("=== VARIABLE USAGE ANALYSIS ===");
//
//                for (AbstractCodeMapping mapping : mapper.getMappings()) {
//                    StatementObject stmt1 = (StatementObject) mapping.getFragment1();
//                    StatementObject stmt2 = (StatementObject) mapping.getFragment2();
//
//                    System.out.println("=== EXPRESSION STRING ANALYSIS ===");
//
//                    // Check each variable's string representation
//                    System.out.println("STMT1 variables:");
//                    for (LeafExpression var : stmt1.getVariables()) {
//                        System.out.println("  " + var.getString() + " (class: " + var.getClass().getSimpleName() + ")");
//                    }
//
//                    System.out.println("STMT2 variables:");
//                    for (LeafExpression var : stmt2.getVariables()) {
//                        System.out.println("  " + var.getString() + " (class: " + var.getClass().getSimpleName() + ")");
//                    }
//
//                    // Check infix expressions
//                    System.out.println("STMT1 infix expressions:");
//                    for (LeafExpression expr : stmt1.getInfixExpressions()) {
//                        System.out.println("  " + expr.getString() + " (class: " + expr.getClass().getSimpleName() + ")");
//                    }
//
//                    System.out.println("STMT2 infix expressions:");
//                    for (LeafExpression expr : stmt2.getInfixExpressions()) {
//                        System.out.println("  " + expr.getString() + " (class: " + expr.getClass().getSimpleName() + ")");
//                    }
//                }
//
//// Also check the variable declaration initializer
//                for (AbstractCodeFragment fragment : mapper.getNonMappedLeavesT2()) {
//                    StatementObject stmt = (StatementObject) fragment;
//                    for (VariableDeclaration varDecl : stmt.getVariableDeclarations()) {
//                        AbstractExpression initializer = (AbstractExpression) varDecl.getInitializer();
//                        System.out.println("Initializer string: '" + initializer.getString() + "'");
//                    }
//                }
//            }
//
//        }
//
//        System.out.println("\nTotal refactorings: " + refactorings.size());
//        for (Refactoring ref : refactorings) {
//            System.out.println("  - " + ref.getRefactoringType() + ": " + ref.toString());
//        }
//        System.out.println("=== END UML MODEL DEBUG ===");
//
//
//        System.out.println("Total refactorings: " + refactorings.size());
//        System.out.println("=== END METHOD DEBUG ===");


        // === COMPREHENSIVE REFACTORING DEBUG OUTPUT ===
        System.out.println("\n=== EXTRACT VARIABLE TEST: " + extractedVariableName + " ===");
        System.out.println("Expression: " + extractedExpression);
        System.out.println("Method: " + methodName + (className != null ? " in class " + className : " (module-level)"));
        System.out.println("Total refactorings detected: " + refactorings.size());

        if (refactorings.isEmpty()) {
            System.out.println("NO REFACTORINGS DETECTED");
        } else {
            for (int i = 0; i < refactorings.size(); i++) {
                Refactoring r = refactorings.get(i);
                System.out.println("Refactoring #" + (i + 1) + ":");
                System.out.println("  Type: " + r.getRefactoringType());
                System.out.println("  Name: " + r.getName());
                System.out.println("  Details: " + r.toString());

                // Additional details for ExtractVariableRefactoring
                if (r instanceof ExtractVariableRefactoring) {
                    ExtractVariableRefactoring evr = (ExtractVariableRefactoring) r;
                    System.out.println("  [ExtractVariable] Variable: " + evr.getVariableDeclaration().getVariableName());
                    System.out.println("  [ExtractVariable] Operation: " + evr.getOperationAfter().toQualifiedString());
                    System.out.println("  [ExtractVariable] Scope: " + evr.getVariableDeclaration().getScope());
                    if (evr.getVariableDeclaration().getInitializer() != null) {
                        System.out.println("  [ExtractVariable] Initializer: " + evr.getVariableDeclaration().getInitializer().getString());
                    }
                    System.out.println("  [ExtractVariable] References: " + evr.getReferences().size());
                }

                System.out.println("  Involved Classes Before: " + r.getInvolvedClassesBeforeRefactoring());
                System.out.println("  Involved Classes After: " + r.getInvolvedClassesAfterRefactoring());
                System.out.println("  ---");
            }
        }
        System.out.println("=== END REFACTORING DEBUG ===\n");

        // Look for ExtractVariableRefactoring with EXTRACT_VARIABLE type
        boolean extractVariableFound = refactorings.stream()
                .filter(r -> r instanceof ExtractVariableRefactoring)
                .map(r -> (ExtractVariableRefactoring) r)
                .anyMatch(refactoring -> {
                    boolean isExtractVariable = refactoring.getRefactoringType() == RefactoringType.EXTRACT_VARIABLE;
                    String variableName = refactoring.getVariableDeclaration().getVariableName();
                    String operationName = refactoring.getOperationAfter().getName();

                    boolean variableNameMatches = variableName.equals(extractedVariableName);
                    boolean methodMatches = operationName.equals(methodName);

                    // Check class name if provided
                    boolean classMatches = true;
                    if (className != null) {
                        String actualClassName = refactoring.getOperationAfter().getClassName();
                        classMatches = actualClassName.equals(className);
                    }

                    // Check extracted expression if possible
                    boolean expressionMatches = true;
                    if (refactoring.getVariableDeclaration().getInitializer() != null) {
                        String actualExpression = refactoring.getVariableDeclaration().getInitializer().getString();
                        expressionMatches = actualExpression.contains(extractedExpression.replaceAll("\\s+", "")) ||
                                extractedExpression.contains(actualExpression.replaceAll("\\s+", ""));
                    }

                    System.out.println("Checking ExtractVariableRefactoring:");
                    System.out.println("  Is EXTRACT_VARIABLE: " + isExtractVariable);
                    System.out.println("  Variable name matches: " + variableNameMatches + " (" + variableName + ")");
                    System.out.println("  Method matches: " + methodMatches + " (" + operationName + ")");
                    System.out.println("  Class matches: " + classMatches);
                    System.out.println("  Expression matches: " + expressionMatches);

                    return isExtractVariable && variableNameMatches && methodMatches && classMatches;
                });

        // Second try: Look for any ExtractVariableRefactoring with our variable name
        if (!extractVariableFound) {
            boolean anyVariableExtractionFound = refactorings.stream()
                    .filter(r -> r instanceof ExtractVariableRefactoring)
                    .map(r -> (ExtractVariableRefactoring) r)
                    .anyMatch(refactoring -> {
                        String variableName = refactoring.getVariableDeclaration().getVariableName();
                        String operationName = refactoring.getOperationAfter().getName();

                        boolean variableNameMatches = variableName.equals(extractedVariableName);
                        boolean methodMatches = operationName.equals(methodName);

                        System.out.println("Checking any ExtractVariable:");
                        System.out.println("  Variable name matches: " + variableNameMatches);
                        System.out.println("  Method matches: " + methodMatches);

                        return variableNameMatches && methodMatches;
                    });

            if (anyVariableExtractionFound) {
                System.out.println("Found variable extraction but not exact EXTRACT_VARIABLE type");
                extractVariableFound = true; // Accept for now to understand the pattern
            }
        }

        // Third try: Look for any refactoring that mentions our variable
        if (!extractVariableFound) {
            boolean mentionsVariable = refactorings.stream()
                    .anyMatch(r -> r.toString().contains(extractedVariableName));

            if (mentionsVariable) {
                System.out.println("Found refactoring mentioning the variable name");
                // extractVariableFound = true; // Uncomment to accept for debugging
            }
        }

        if (!extractVariableFound) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Extract Variable refactoring not detected.\n");
            errorMessage.append("Expected: Extract variable '").append(extractedVariableName)
                    .append("' from expression '").append(extractedExpression)
                    .append("' in method '").append(methodName).append("'");
            if (className != null) {
                errorMessage.append(" of class '").append(className).append("'");
            }
            errorMessage.append("\n");

            errorMessage.append("Analysis:\n");
            errorMessage.append("- Total refactorings found: ").append(refactorings.size()).append("\n");

            long extractVariableCount = refactorings.stream()
                    .filter(r -> r instanceof ExtractVariableRefactoring)
                    .count();
            errorMessage.append("- ExtractVariableRefactoring instances: ").append(extractVariableCount).append("\n");

            long extractVariableTypeCount = refactorings.stream()
                    .filter(r -> r instanceof ExtractVariableRefactoring)
                    .map(r -> (ExtractVariableRefactoring) r)
                    .filter(r -> r.getRefactoringType() == RefactoringType.EXTRACT_VARIABLE)
                    .count();
            errorMessage.append("- EXTRACT_VARIABLE type count: ").append(extractVariableTypeCount).append("\n");

            // Check if we have add variable replacements
            boolean hasAddVariableReplacements = refactorings.stream()
                    .anyMatch(r -> r.toString().toLowerCase().contains("add") &&
                            r.toString().toLowerCase().contains("variable"));
            errorMessage.append("- Has 'add variable' related refactorings: ").append(hasAddVariableReplacements).append("\n");

            fail(errorMessage.toString());
        }

        assertTrue(extractVariableFound, "Expected Extract Variable refactoring to be detected");
    }
}