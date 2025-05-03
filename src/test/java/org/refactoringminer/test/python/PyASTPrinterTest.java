package org.refactoringminer.test.python;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.refactoringminer.utils.LangASTUtil;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Isolated
public class PyASTPrinterTest {

    @Test
    public void testASTVisitorSimple() {
        String code = "x = 10\n";
        LangASTUtil.printAST(code);
    }

    @Test
    public void testASTVisitor() {
        // Example Python code
        String code = "class Calculator:\n" +
                "    def add(self, x, y):\n" +
                "        x = x + y\n" +
                "        return x";
        LangASTUtil.printAST(code);
    }

    @Test
    public void testASTVisitor_IfStatement() {
        // Example Python code with if statement
        String code = "class Calculator:\n" +
                "    def add(self, x, y):\n" +
                "        if x > y:\n" +
                "            return x\n" +
                "        else:\n" +
                "            return y";

        LangASTUtil.printAST(code);
    }

    @Test
    public void testASTVisitor_ForLoop() {
        // Example Python code with for loop
        String code = "class ListProcessor:\n" +
                "    def process_numbers(self, numbers):\n" +
                "        sum = 0\n" +
                "        for num in numbers:\n" +
                "            sum = sum + num\n" +
                "        return sum";

        LangASTUtil.printAST(code);
    }

    @Test
    public void testASTVisitor_WhileLoop() {
        // Example Python code with for while
        String code = "class Counter:\n" +
                "    def count_up(self, limit):\n" +
                "        i = 0\n" +
                "        while i < limit:\n" +
                "            i = i + 1\n" +
                "            print(i)\n" +
                "        return i";

        LangASTUtil.printAST(code);
    }

    @Test
    public void testASTVisitor_MultipleMethods() {
        // Example Python code with for while
        String code = "class Counter:\n" +
                "    def count_up(self, limit):\n" +
                "        i = 0\n" +
                "        while i < limit:\n" +
                "            i = i + 1\n" +
                "            print(i)\n" +
                "        return i\n" +
                "       \n" +
                "    def process_numbers(self, numbers):\n" +
                "        sum = 0\n" +
                "        for num in numbers:\n" +
                "            sum = sum + num\n" +
                "        return sum";

        LangASTUtil.printAST(code);
    }


}
