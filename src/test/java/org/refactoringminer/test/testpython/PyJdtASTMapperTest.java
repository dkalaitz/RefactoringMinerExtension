package org.refactoringminer.test.testpython;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.refactoringminer.utils.LangASTUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Isolated
public class PyJdtASTMapperTest {

    @Test
    public void testPyMapper(){
        String code = "class Calculator:\n" +
                "    def add(self, x, y):\n" +
                "        x = x + y\n" +
                "        return x";

        ASTNode ast = LangASTUtil.mapToJdt(code);
        assertNotNull(ast, "The JDT AST should not be null");
        System.out.println("Successfully mapped Python code to JDT AST");


        assertInstanceOf(CompilationUnit.class, ast, "Root node should be a CompilationUnit");
        System.out.println("Successfully asserted that root node is a CompilationUnit");

        CompilationUnit cu = (CompilationUnit) ast;
        List<?> types = cu.types();
        assertFalse(types.isEmpty(), "No types found in the compilation unit");

    }

}

