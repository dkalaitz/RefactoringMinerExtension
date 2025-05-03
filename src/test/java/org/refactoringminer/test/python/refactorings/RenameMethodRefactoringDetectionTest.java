package org.refactoringminer.test.python.refactorings;

import antlr.umladapter.PythonUMLModelAdapter;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.UMLParameter;
import gr.uom.java.xmi.decomposition.CompositeStatementObject;
import gr.uom.java.xmi.decomposition.OperationBody;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RenameMethodRefactoringDetectionTest {

    @Test
    void detectsMethodRename_SumToAdd() throws Exception {
        String beforePythonCode = """
            class Calculator:
                def sum(self, x, y):
                    return x + y
            """;
        String afterPythonCode = """
            class Calculator:
                def add(self, x, y):
                    return x + y
            """;
        Map<String, String> beforeFiles = Map.of("tests/before/calculator.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/after/calculator.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "sum", "add");
    }

    @Test
    void detectsMethodRename_GreetToSayHello() throws Exception {
        String beforePythonCode = """
            class Greeter:
                def greet(self, name):
                    return "Hello, " + name
            """;
        String afterPythonCode = """
            class Greeter:
                def say_hello(self, name):
                    return "Hello, " + name
            """;
        Map<String, String> beforeFiles = Map.of("tests/before/greeter.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/after/greeter.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "greet", "say_hello");
    }

    @Test
    void detectsMethodRename_SpeakToCommunicate() throws Exception {
        String beforePythonCode = """
            class Animal:
                def speak(self):
                    return "noise"
            """;
        String afterPythonCode = """
            class Animal:
                def communicate(self):
                    return "noise"
            """;
        Map<String, String> beforeFiles = Map.of("tests/before/animal.py", beforePythonCode);
        Map<String, String> afterFiles = Map.of("tests/after/animal.py", afterPythonCode);
        assertRenameOperationRefactoringDetected(beforeFiles, afterFiles, "speak", "communicate");
    }

    private void assertRenameOperationRefactoringDetected(Map<String, String> beforeFiles, Map<String, String> afterFiles, String beforeName, String afterName) throws Exception {
        UMLModel beforeUML = new PythonUMLModelAdapter(beforeFiles).getUMLModel();
        UMLModel afterUML = new PythonUMLModelAdapter(afterFiles).getUMLModel();

        System.out.println("=== BEFORE MODEL OPERATIONS ===");
        beforeUML.getClassList().forEach(umlClass -> {
            System.out.println("Class: " + umlClass.getName());
            for (UMLOperation op : umlClass.getOperations()) {
                System.out.println("  " + dumpOperation(op));
            }
        });

        System.out.println("=== AFTER MODEL OPERATIONS ===");
        afterUML.getClassList().forEach(umlClass -> {
            System.out.println("Class: " + umlClass.getName());
            for (UMLOperation op : umlClass.getOperations()) {
                System.out.println("  " + dumpOperation(op));
            }
        });


        UMLModelDiff diff = beforeUML.diff(afterUML);
        boolean methodRenameDetected = diff.getRefactorings().stream()
                .anyMatch(ref -> {
                    if (ref instanceof RenameOperationRefactoring renameRef) {
                        UMLOperation originalOperation = renameRef.getOriginalOperation();
                        UMLOperation renamedOperation = renameRef.getRenamedOperation();

                        return originalOperation.getName().equals(beforeName) &&
                                renamedOperation.getName().equals(afterName);
                    }
                    return false;
                });

        System.out.println("==== DIFF ====");
        System.out.println("Method rename detected: " + methodRenameDetected);
        System.out.println("Total refactorings: " + diff.getRefactorings().size());

        diff.getRefactorings().forEach(System.out::println);
        System.out.println("\n");

        assertTrue(methodRenameDetected, "Expected a RenameMethodRefactoring from " + beforeName + " to " + afterName);
    }

    private static String dumpOperation(UMLOperation op) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "name=%s, params=%s, signature=%s, isConstructor=%b, static=%b, visibility=%s",
                op.getName(),
                op.getParameters().stream().map(UMLParameter::getName).toList(),
                op.getActualSignature(),
                op.isConstructor(),
                op.isStatic(),
                op.getVisibility()
        ));

        // Enhanced body logging
        OperationBody body = op.getBody();
        if (body != null) {
            sb.append("\n    BODY: hashCode=").append(body.getBodyHashCode());

            CompositeStatementObject composite = body.getCompositeStatement();
            sb.append("\n    STATEMENTS: ").append(composite.getStatements().size())
                    .append(", LEAVES: ").append(composite.getLeaves().size())
                    .append(", INNER NODES: ").append(composite.getInnerNodes().size())
                    .append(", EXPRESSIONS: ").append(composite.getExpressions().size());

            sb.append("\n    VARIABLES: ").append(body.getAllVariables());
            sb.append("\n    METHOD CALLS: ").append(body.getAllOperationInvocations().size());

            sb.append("\n    STRING REPRESENTATION:");
            List<String> stringRep = body.stringRepresentation();
            for (String line : stringRep) {
                sb.append("\n      ").append(line);
            }
        } else {
            sb.append("\n    BODY: null");
        }

        return sb.toString();
    }

}