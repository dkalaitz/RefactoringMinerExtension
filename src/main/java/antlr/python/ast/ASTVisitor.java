package antlr.python.ast;

import antlr.python.node.child.*;

// Visitor interface for traversing the AST
public interface ASTVisitor {
    void visit(CompilationUnit compilationUnit);
    void visit(TypeDeclaration typeDeclaration);
    void visit(MethodDeclaration methodDeclaration);
    void visit(Parameter parameter);
}