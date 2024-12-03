package antlr.python.ast;

import antlr.python.Python3Parser;
import antlr.python.Python3ParserBaseVisitor;
import antlr.python.ast.elements.*;

import java.util.ArrayList;
import java.util.List;

// Class that transforms the ParseTree to an AST
public class PythonASTBuilder extends Python3ParserBaseVisitor<ASTNode> {

    @Override
    public ASTNode visitFile_input(Python3Parser.File_inputContext ctx) {
        System.out.println("Visiting File_input...");
        List<ASTNode> body = new ArrayList<>();
        for (Python3Parser.StmtContext stmt : ctx.stmt()) {
            System.out.println("Visiting Stmt...");
            ASTNode stmtNode = visit(stmt);
            if (stmtNode != null) {
                body.add(stmtNode);  // Add non-null statements to body
            }
        }
        System.out.println("Returning ModuleNode with body size: " + body.size());
        return new ModuleNode(body);
    }

    @Override
    public ASTNode visitFuncdef(Python3Parser.FuncdefContext ctx) {
        String name = ctx.name().getText();  // Function name
        System.out.println("Visiting Funcdef: " + name);
        List<ASTNode> params = new ArrayList<>();
        List<ASTNode> body = new ArrayList<>();

        // Process parameters (simplified here for demonstration)
        if (ctx.parameters() != null && ctx.parameters().typedargslist() != null) {
            System.out.println("Visiting Parameters...");
            for (Python3Parser.TfpdefContext paramCtx : ctx.parameters().typedargslist().tfpdef()) {
                System.out.println("Adding Parameter: " + paramCtx.getText());
                params.add(new ParamNode(paramCtx.getText()));  // Add each parameter
            }
        }

        // Process function body
        System.out.println("Visiting Function Body...");
        for (Python3Parser.StmtContext stmtCtx : ctx.block().stmt()) {
            ASTNode stmtNode = visit(stmtCtx);
            if (stmtNode != null) {
                body.add(stmtNode);
            }
        }

        System.out.println("Returning FunctionDefNode: " + name);
        return new FunctionDefNode(name, params, body);
    }

    @Override
    public ASTNode visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {
        System.out.println("Visiting Expr_stmt: " + ctx.getText());
        return new ExpressionNode(ctx.getText());
    }

    @Override
    public ASTNode visitClassdef(Python3Parser.ClassdefContext ctx) {
        String name = ctx.name().getText();  // Class name
        System.out.println("Visiting Classdef: " + name);
        List<ASTNode> body = new ArrayList<>();

        // Process class body
        System.out.println("Visiting Class Body...");
        for (Python3Parser.StmtContext stmtCtx : ctx.block().stmt()) {
            ASTNode stmtNode = visit(stmtCtx);
            if (stmtNode != null) {
                body.add(stmtNode);
            }
        }

        System.out.println("Returning ClassNode: " + name);
        return new ClassNode(name, body);
    }

    // Visit a `while` statement in the parse tree
    @Override
    public ASTNode visitWhile_stmt(Python3Parser.While_stmtContext ctx) {
        List<ASTNode> body = new ArrayList<>();

        // Visit the condition (test expression)
        ASTNode condition = visit(ctx.test());

        // Visit each block in the while loop (there may be multiple blocks)
        for (Python3Parser.BlockContext blockCtx : ctx.block()) {
            // A block contains statements
            for (Python3Parser.StmtContext stmtCtx : blockCtx.stmt()) {
                ASTNode stmtNode = visit(stmtCtx);
                if (stmtNode != null) {
                    body.add(stmtNode);  // Add each non-null statement to the body
                }
            }
        }

        // Return the WhileNode with the condition and body
        return new WhileNode(condition, body);
    }

    @Override
    public ASTNode visitFor_stmt(Python3Parser.For_stmtContext ctx) {
        List<ASTNode> body = new ArrayList<>();
        List<ASTNode> iterables = new ArrayList<>();

        // Visit the expression list (for variables being iterated over)
        if (ctx.exprlist() != null) {
            ASTNode exprListNode = visit(ctx.exprlist());  // Visit the expression list (e.g., variables to loop over)
            iterables.add(exprListNode);
        }

        // Visit the test list (the iterable, such as a range, list, etc.)
        if (ctx.testlist() != null) {
            ASTNode testListNode = visit(ctx.testlist());  // Visit the iterable (e.g., range(10))
            iterables.add(testListNode);
        }

        // Visit each block in the for loop (there can be multiple blocks)
        for (Python3Parser.BlockContext blockCtx : ctx.block()) {
            // Each block contains one or more statements
            for (Python3Parser.StmtContext stmtCtx : blockCtx.stmt()) {
                ASTNode stmtNode = visit(stmtCtx);
                if (stmtNode != null) {
                    body.add(stmtNode);  // Add each non-null statement to the body
                }
            }
        }

        // If there's an else block in the for loop, handle it
        if (ctx.ELSE() != null) {
            List<ASTNode> elseBody = new ArrayList<>();
            for (Python3Parser.BlockContext blockCtx : ctx.block()) {
                for (Python3Parser.StmtContext stmtCtx : blockCtx.stmt()) {
                    ASTNode stmtNode = visit(stmtCtx);
                    if (stmtNode != null) {
                        elseBody.add(stmtNode);
                    }
                }
            }
            // Optionally, add an else node to the body
            body.add(new ElseNode(elseBody));  // Create and add an ElseNode to the body
        }

        // Return the ForNode with iterables and body
        return new ForNode(iterables, body);
    }


    // Visit an `if` statement in the parse tree
    @Override
    public ASTNode visitIf_stmt(Python3Parser.If_stmtContext ctx) {
        List<ASTNode> body = new ArrayList<>();
        List<ElifNode> elifs = new ArrayList<>();
        List<ASTNode> elseBody = new ArrayList<>();

        // Debugging: print the number of conditions (tests)
        System.out.println("Number of test conditions: " + ctx.test().size());

        // Visit the condition for the "if" part
        ASTNode ifCondition = null;
        if (ctx.test(0) != null) {
            System.out.println("Visiting 'if' condition: " + ctx.test(0).getText());
            ifCondition = visit(ctx.test(0));  // First test condition for 'if'
        } else {
            System.out.println("Error: 'if' condition is null");
        }

        // Check if ifCondition is null after visiting
        if (ifCondition == null) {
            System.out.println("Error: ifCondition is still null after visiting");
        }

        // Visit the body of the "if" block
        if (ctx.block(0) != null) {
            for (Python3Parser.StmtContext stmtCtx : ctx.block(0).stmt()) {
                ASTNode stmtNode = visit(stmtCtx);
                if (stmtNode != null) {
                    body.add(stmtNode);
                }
            }
        }

        // Visit the "elif" parts
        for (int i = 0; i < ctx.ELIF().size(); i++) {
            ASTNode elifCondition = visit(ctx.test(i + 1));  // Elif conditions
            List<ASTNode> elifBody = new ArrayList<>();
            if (ctx.block(i + 1) != null) {
                for (Python3Parser.StmtContext stmtCtx : ctx.block(i + 1).stmt()) {
                    ASTNode stmtNode = visit(stmtCtx);
                    if (stmtNode != null) {
                        elifBody.add(stmtNode);
                    }
                }
            }
            elifs.add(new ElifNode(elifCondition, elifBody));
        }

        // Visit the "else" part (if exists)
        if (ctx.ELSE() != null) {
            if (ctx.block(ctx.ELIF().size() + 1) != null) {
                for (Python3Parser.StmtContext stmtCtx : ctx.block(ctx.ELIF().size() + 1).stmt()) {
                    ASTNode stmtNode = visit(stmtCtx);
                    if (stmtNode != null) {
                        elseBody.add(stmtNode);
                    }
                }
            }
        }

        // Create and return the IfNode with the gathered components
        return new IfNode(ifCondition, body, elifs, elseBody);
    }

  /*  @Override
    public ASTNode visitTest(Python3Parser.TestContext ctx) {
        System.out.println("Visiting test: " + ctx.getText()); // Debugging line

        // Check the type of test being handled
        if (ctx.or_test() != null) {
            return visit(ctx.or_test());  // Visit logical OR tests
        } else if (ctx.and_test() != null) {
            return visit(ctx.and_test());  // Visit logical AND tests
        } else if (ctx.comparison() != null) {
            return visit(ctx.comparison());  // Visit comparisons like a > b
        }

        return null;
    }*/

    @Override
    public ASTNode visitOr_test(Python3Parser.Or_testContext ctx) {
        // Handle or_test (logical OR)
        System.out.println("Visiting or_test: " + ctx.getText());
        return visit(ctx.and_test(0));  // Visit the first and_test in the or_test
    }

    @Override
    public ASTNode visitAnd_test(Python3Parser.And_testContext ctx) {
        // Handle and_test (logical AND)
        System.out.println("Visiting and_test: " + ctx.getText());
        return visit(ctx.not_test(0));  // Visit the first not_test in the and_test
    }

    @Override
    public ASTNode visitComparison(Python3Parser.ComparisonContext ctx) {
        // Handle comparisons like `a > b`
        System.out.println("Visiting comparison: " + ctx.getText());
        return visit(ctx.expr(0));  // Visit the first expression in the comparison
    }


    // Additional visit methods for other Python constructs
}
