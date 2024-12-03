package antlr.python.ast;

import antlr.python.Python3Parser;
import antlr.python.ast.elements.*;

public class ASTPrintVisitor implements PythonASTVisitor {
    private int indent = 0;

    private void printIndent() {
        for (int i = 0; i < indent; i++) {
            System.out.print("  ");  // Print indentation
        }
    }

    @Override
    public void visit(FunctionDefNode node) {
        printIndent();
        System.out.println("FunctionDefNode: " + node.name);
        indent++;
        for (ASTNode param : node.params) {
            param.accept(this);  // Visit each parameter
        }
        for (ASTNode bodyNode : node.body) {
            bodyNode.accept(this);  // Visit each body node
        }
        indent--;
    }

    @Override
    public void visit(ClassNode node) {
        printIndent();
        System.out.println("ClassNode: " + node.name);
        indent++;
        for (ASTNode bodyNode : node.body) {
            bodyNode.accept(this);  // Visit each body node
        }
        indent--;
    }

    @Override
    public void visit(ExpressionNode node) {
        printIndent();
        System.out.println("ExpressionNode: " + node.expression);
    }

    @Override
    public void visit(ParamNode node) {
        printIndent();
        System.out.println("ParamNode: " + node.paramName);
    }

    @Override
    public void visit(ModuleNode node) {
        printIndent();
        System.out.println("ModuleNode:");
        indent++;
        for (ASTNode bodyNode : node.body) {
            bodyNode.accept(this);  // Visit each element in the module body
        }
        indent--;
    }

    @Override
    public void visit(WhileNode node) {
        printIndent();
        System.out.println("While Node:");
        indent++;

        // Visit the condition of the while loop
        printIndent();
        System.out.print("Condition: ");
        node.condition.accept(this);

        // Visit the body of the while loop
        printIndent();
        System.out.println("Body:");
        for (ASTNode stmt : node.body) {
            stmt.accept(this);
        }

        indent--;
    }


    @Override
    public void visit(ForNode node) {
        printIndent();
        System.out.println("For loop:");

        indent++;
        // Print iterables (e.g., variables being iterated over)
        printIndent();
        System.out.println("Iterables:");
        for (ASTNode iterable : node.getIterables()) {
            iterable.accept(this);
        }

        // Print body of the for loop
        printIndent();
        System.out.println("Body:");
        for (ASTNode stmt : node.getBody()) {
            stmt.accept(this);
        }
        indent--;
    }



    @Override
    public void visit(IfNode node) {
        printIndent();
        System.out.println("IfNode:");

        // Handle the condition and ensure it's not null before visiting
        if (node.condition != null) {
            node.condition.accept(this);
        } else {
            System.out.println("  Condition is null");
        }

        indent++;
        for (ASTNode bodyNode : node.body) {
            bodyNode.accept(this);
        }

        for (ElifNode elifNode : node.elifs) {
            elifNode.accept(this);
        }

        for (ASTNode elseStmt : node.elseBody) {
            elseStmt.accept(this);
        }
        indent--;
    }



    @Override
    public void visit(ElseNode node) {
        printIndent();
        System.out.println("Else Node:");
        indent++;

        // Visit the body of the else block
        for (ASTNode stmt : node.body) {
            stmt.accept(this);
        }

        indent--;
    }


    @Override
    public void visit(ElifNode node) {
        printIndent();
        System.out.println("Elif Node:");
        indent++;

        // Visit the condition for the "elif" part
        printIndent();
        System.out.print("Condition: ");
        node.condition.accept(this);

        // Visit the body of the "elif" block
        printIndent();
        System.out.println("Body:");
        for (ASTNode stmt : node.body) {
            stmt.accept(this);
        }

        indent--;
    }


    @Override
    public Void visitASTNode(ASTNode node) {
        // Handle generic ASTNode visit, if necessary
        return null;
    }

    @Override
    public Void visitIfNode(IfNode node) {
        System.out.println("Visiting IfNode:");
        if (node.condition != null) {
            System.out.println("  Condition: ");
            node.condition.accept(this);  // Visit the condition of the if node
        }
        for (ASTNode bodyNode : node.body) {
            bodyNode.accept(this);  // Visit each statement in the body
        }
        for (ElifNode elifNode : node.elifs) {
            elifNode.accept(this);  // Visit each elif branch
        }
        for (ASTNode elseStmt : node.elseBody) {
            elseStmt.accept(this);  // Visit each statement in the else body
        }
        return null;
    }

    @Override
    public Void visitElifNode(ElifNode node) {
        System.out.println("Visiting ElifNode:");
        if (node.condition != null) {
            node.condition.accept(this);  // Visit the condition for elif
        }
        for (ASTNode elifStmt : node.body) {
            elifStmt.accept(this);  // Visit statements in the elif block
        }
        return null;
    }

    @Override
    public Void visit(Python3Parser.TestContext ctx) {
        return null;
    }

    /*@Override
    public Void visit(Python3Parser.TestContext ctx) {
        System.out.println("Visiting TestContext: " + ctx.getText());
        // Visit or_test, lambdef, or nested tests if present
        if (ctx.or_test() != null) {
            visit(ctx.or_test(0));  // Visit or_test (logical OR tests)
        }
        if (ctx.test() != null) {
            visit(ctx.test());  // Visit nested test
        }
        if (ctx.lambdef() != null) {
            visit(ctx.lambdef());  // Visit lambda function, if any
        }
        return null;
    }*/

}



