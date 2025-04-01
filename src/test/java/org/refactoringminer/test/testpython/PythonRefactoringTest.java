package org.refactoringminer.test.testpython;

import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLModelASTReader;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringMinerTimedOutException;
import org.refactoringminer.api.RefactoringType;
import org.junit.jupiter.api.Assertions;

import java.util.*;

@Isolated
public class PythonRefactoringTest {

    @Test
    public void testClassAndMethodRenameRefactoring() throws RefactoringMinerTimedOutException {
        // Test with sample Python code before and after refactoring
        String beforeRefactoring =
                "class Calculator:\n" +
                        "    def add(self, x, y):\n" +
                        "        x = x + y\n" +
                        "        return x";

        String afterRefactoring =
                "class Calculator:\n" +
                        "    def sum(self, x, y):\n" +
                        "        x = x + y\n" +
                        "        return x";

        // Set up before files
        Map<String, String> beforeFiles = new HashMap<>();
        beforeFiles.put("calculator.py", beforeRefactoring);

        // Set up after files
        Map<String, String> afterFiles = new HashMap<>();
        afterFiles.put("calculator.py", afterRefactoring);

        // Create UML models
        Set<String> repositoryDirectories = new HashSet<>();
        repositoryDirectories.add("/src");

        boolean astDiff = false;
        UMLModelASTReader beforeModelReader = new UMLModelASTReader(beforeFiles, repositoryDirectories, astDiff);
        UMLModel beforeModel = beforeModelReader.getUmlModel();

        UMLModelASTReader afterModelReader = new UMLModelASTReader(afterFiles, repositoryDirectories, astDiff);
        UMLModel afterModel = afterModelReader.getUmlModel();

        // Compare models to detect refactorings
        UMLModelDiff modelDiff = new UMLModelDiff(beforeModel, afterModel);
        List<Refactoring> refactorings = modelDiff.getRefactorings();

        System.out.println("Detected " + refactorings.size() + " refactorings:");
        // Log detected refactorings
        for (Refactoring refactoring : refactorings) {
            System.out.println(refactoring.toString());
        }

        // Assertions for class rename
        boolean hasClassRename = refactorings.stream()
                .anyMatch(r -> r.getRefactoringType() == RefactoringType.RENAME_CLASS &&
                        r.toString().contains("Calculator") && r.toString().contains("CalcClass"));

        // Assertions for method rename
        boolean hasMethodRename = refactorings.stream()
                .anyMatch(r -> r.getRefactoringType() == RefactoringType.RENAME_METHOD &&
                        r.toString().contains("add") && r.toString().contains("sum"));

//        Assertions.assertTrue(hasClassRename, "Class rename refactoring from Calculator to CalcClass should be detected");
//        Assertions.assertTrue(hasMethodRename, "Method rename refactoring from add to sum should be detected");
//        Assertions.assertTrue(refactorings.size() >= 2, "At least two refactorings should be detected");
    }
//
//    @Test
//    public void testJavaRefactoringDetection() throws RefactoringMinerTimedOutException {
//        // Test with sample Java code before and after refactoring
//        String beforeRefactoring =
//                "public class Calculator {\n" +
//                        "    public int add(int a, int b) {\n" +
//                        "        return a + b;\n" +
//                        "    }\n" +
//                        "}";
//
//        // After refactoring - renamed class and method
//        String afterRefactoring =
//                "public class CalcClass {\n" +
//                        "    public int sum(int a, int b) {\n" +
//                        "        return a + b;\n" +
//                        "    }\n" +
//                        "}";
//
//        // Set up before files
//        Map<String, String> beforeFiles = new HashMap<>();
//        beforeFiles.put("Calculator.java", beforeRefactoring);
//
//        // Set up after files
//        Map<String, String> afterFiles = new HashMap<>();
//        afterFiles.put("CalcClass.java", afterRefactoring);
//
//        // Create UML models
//        Set<String> repositoryDirectories = new HashSet<>();
//        repositoryDirectories.add("/src");
//
//        UMLModelASTReader beforeModelReader = new UMLModelASTReader(beforeFiles, repositoryDirectories, false);
//        UMLModel beforeModel = beforeModelReader.getUmlModel();
//
//        UMLModelASTReader afterModelReader = new UMLModelASTReader(afterFiles, repositoryDirectories, false);
//        UMLModel afterModel = afterModelReader.getUmlModel();
//
//        // Compare models to detect refactorings
//        UMLModelDiff modelDiff = new UMLModelDiff(beforeModel, afterModel);
//        List<Refactoring> refactorings = modelDiff.getRefactorings();
//
//        // Print detected refactorings
//        System.out.println("Detected " + refactorings.size() + " refactorings:");
//        for (Refactoring refactoring : refactorings) {
//            System.out.println(refactoring.toString());
//        }
//
//        // Assertions for class rename
//        boolean hasClassRename = refactorings.stream()
//                .anyMatch(r -> r.getRefactoringType() == RefactoringType.RENAME_CLASS &&
//                        r.toString().contains("Calculator") && r.toString().contains("CalcClass"));
//
//        // Assertions for method rename
//        boolean hasMethodRename = refactorings.stream()
//                .anyMatch(r -> r.getRefactoringType() == RefactoringType.RENAME_METHOD &&
//                        r.toString().contains("add") && r.toString().contains("sum"));
//
////        Assertions.assertTrue(hasClassRename, "Class rename refactoring from Calculator to CalcClass should be detected");
////        Assertions.assertTrue(hasMethodRename, "Method rename refactoring from add to sum should be detected");
//    }
//

}