package antlr.python.ast.visitor;

import antlr.python.ast.ASTVisitor;
import antlr.python.node.ASTNode;
import antlr.python.node.child.*;

// Concrete visitor to print the AST structure
public class ASTPrinter implements ASTVisitor {
    private int indentation = 0;

    private void printIndented(String message) {
        System.out.println("  ".repeat(indentation) + message);
    }

    @Override
    public void visit(CompilationUnit compilationUnit) {
        printIndented("CompilationUnit");
        indentation++;
    }

    @Override
    public void visit(TypeDeclaration typeDeclaration) {
        printIndented("TypeDeclaration: " + typeDeclaration.getName());
        indentation++;
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        printIndented("MethodDeclaration: " + methodDeclaration.getName());
        indentation++;
    }

    @Override
    public void visit(Parameter parameter) {
        printIndented("Parameter: " + parameter.getName());
    }

    // Reduce indentation when leaving a node
    public void postVisit(ASTNode node) {
        indentation--;
    }
}