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
    public void testASTVisitor_WithComments() {
        // Example Python code
        String code = "class Calculator:\n" +
                "    # Comment one line\n" +
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

    @Test
    public void testASTVisitor_Literals() {
        String code =
                "class Example:\n" +
                        "    def demonstrate_literals(self):\n" +
                        "        text = \"hello world\"\n" +
                        "        numbers = [1, 2, 3]\n" +
                        "        flag = True\n" +
                        "        return flag\n";


        LangASTUtil.printAST(code);
    }

    @Test
    public void testASTVisitor_MethodInvocation() {
        String code =
                "class Example:\n" +
                        "    def demonstrate_literals(self):\n" +
                        "        text = \"hello world\"\n" +
                        "        print(text)\n" +
                        "        return flag\n";


        LangASTUtil.printAST(code);
    }

    @Test
    public void testASTVisitor_MethodInvocationWithMultipleArguments() {
        String code =
                "class Example:\n" +
                        "    def demonstrate_literals(self):\n" +
                        "        text1 = \"hello world1\"\n" +
                        "        text2 = \"hello world2\"\n" +
                        "        method(text1, text2)\n" +
                        "        flag = false\n" +
                        "        return flag\n";


        LangASTUtil.printAST(code);
    }

    @Test
    public void testASTVisitor_MethodInvocationWithChaining() {
        String code =
                "class Example:\n" +
                        "    def demonstrate_literals(self):\n" +
                        "        text1 = \"hello world1\"\n" +
                        "        text2 = \"hello world2\"\n" +
                        "        method1(text1).method2(text2)\n" +
                        "        flag = false\n" +
                        "        return flag\n";


        LangASTUtil.printAST(code);
    }

    @Test
    public void testASTVisitor_PythonTuple() {
        String code =
                "def test_tuple_operations():\n" +
                        "    coordinates = (10, 20, 30)\n" +
                        "    \n" +
                        "    # Access tuple elements\n" +
                        "    x = coordinates[0]\n" +
                        "    y = coordinates[1]\n" +
                        "    z = coordinates[2]\n" +
                        "    \n" +
                        "    return (x, y + 5, z * 2)\n";

        LangASTUtil.printAST(code);
    }

    @Test
    public void testASTVisitor_PythonDictionary() {
        String code =
                "def test_dictionary_operations():\n" +
                        "    user = {\"name\": \"John\", \"age\": 30, \"is_active\": True}\n" +
                        "    \n" +
                        "    username = user[\"name\"]\n" +
                        "    \n" +
                        "    user[\"last_login\"] = \"2023-07-15\"\n" +
                        "    user[\"age\"] = user[\"age\"] + 1\n" +
                        "    \n" +
                        "    return user\n";

        LangASTUtil.printAST(code);
    }


    @Test
    public void testASTVisitor_PythonMinimalVarargs() {
        String code =
                "def _foo__(*args):\n" +
                        "    pass\n";
        LangASTUtil.printAST(code);
    }






}
