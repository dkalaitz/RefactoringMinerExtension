package antlr.python.ast;

import antlr.python.Python3Parser;
import antlr.python.ast.elements.*;

public interface PythonASTVisitor {
    void visit(FunctionDefNode node);
    void visit(ClassNode node);
    void visit(ExpressionNode node);
    void visit(ParamNode node);
    void visit(ModuleNode moduleNode);
    void visit(WhileNode node);
    void visit(ForNode node);
    void visit(IfNode node);
    void visit(ElseNode node);
    void visit(ElifNode elifNode);

    Void visitASTNode(ASTNode node);

    Void visitIfNode(IfNode node);

    Void visitElifNode(ElifNode node);

    Void visit(Python3Parser.TestContext ctx);
    // Add visit methods for other node types as needed
}

