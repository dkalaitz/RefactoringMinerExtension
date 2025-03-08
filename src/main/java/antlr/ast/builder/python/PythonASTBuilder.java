package antlr.ast.builder.python;

import antlr.ast.node.declaration.MethodDeclaration;
import antlr.ast.node.declaration.TypeDeclaration;
import antlr.ast.node.expression.InfixExpression;
import antlr.ast.node.misc.Parameter;
import antlr.ast.node.statement.Block;
import antlr.ast.node.unit.CompilationUnit;
import antlr.base.python.Python3Parser;
import antlr.base.python.Python3ParserBaseVisitor;
import antlr.ast.node.ASTNode;


import java.util.ArrayList;
import java.util.List;

/**
 * Βuilder class to traverse the ANTLR parse tree
 * and build the custom AST.
 */
public class PythonASTBuilder extends Python3ParserBaseVisitor<ASTNode> {

    @Override
    public ASTNode visitFile_input(Python3Parser.File_inputContext ctx) {
        // Create the root CompilationUnit
        CompilationUnit compilationUnit = ASTNodeFactory.createCompilationUnit(ctx);

        // Visit all statements in the file and add them to the CompilationUnit
        for (Python3Parser.StmtContext stmtCtx : ctx.stmt()) {
            ASTNode child = visit(stmtCtx);
            if (child != null) {
                compilationUnit.addChild(child);
            } else {
                System.out.println("Child is null");
            }
        }
        System.out.println("CompilationUnit: " + compilationUnit.getChildren().toString());
        return compilationUnit;
    }


    @Override
    public ASTNode visitClassdef(Python3Parser.ClassdefContext ctx) {

        TypeDeclaration typeDeclaration = ASTNodeFactory.createTypeDeclaration(ctx);

        if (ctx.block() != null) {
            // If there is a block context
            for (Python3Parser.StmtContext stmtContext : ctx.block().stmt()) {
                ASTNode statement = visit(stmtContext);

                if (statement instanceof MethodDeclaration) {
                    typeDeclaration.addMethod((MethodDeclaration) statement);
                }
            }
        }

        return typeDeclaration;
    }


    @Override
    public ASTNode visitFuncdef(Python3Parser.FuncdefContext ctx) {

        // Collect parameters
        List<Parameter> parameters = new ArrayList<>();
        if (ctx.parameters().typedargslist() != null) {
            for (Python3Parser.TfpdefContext paramCtx : ctx.parameters().typedargslist().tfpdef()) {
                parameters.add(ASTNodeFactory.createParameter(paramCtx.name().getText(), paramCtx));
            }
        }

        // Visit the function body
        Block body = (Block) visit(ctx.block());

        // Create the MethodDeclaration node using the factory
        return ASTNodeFactory.createMethodDeclaration(ctx.name().getText(), ctx, parameters, body);

    }

    @Override
    public ASTNode visitTestlist_star_expr(Python3Parser.Testlist_star_exprContext ctx) {
        // Print the entire subtree text
        System.out.println("Visiting testlist_star_expr: " + ctx.getText());

        // Optionally, inspect individual children
        for (int i = 0; i < ctx.getChildCount(); i++) {
            System.out.println("Child " + i + ": " + ctx.getChild(i).getText());
        }

        // Then continue the regular visit logic
        return super.visitTestlist_star_expr(ctx);
    }

    @Override
    public ASTNode visitStmt(Python3Parser.StmtContext ctx) {
        if (ctx.simple_stmts() != null && !ctx.simple_stmts().isEmpty()) {
            // Use an ArrayList to gather multiple simple statements
            Block statementNodes = ASTNodeFactory.createBlock(ctx, new ArrayList<>());
            for (Python3Parser.Simple_stmtContext simpleStmtContext : ctx.simple_stmts().simple_stmt()) {
                ASTNode stmtNode = visit(simpleStmtContext);
                if (stmtNode != null) {
                    statementNodes.addStatement(stmtNode);
                }
            }

            return statementNodes;
        }
        else if (ctx.compound_stmt() != null) {
            return visit(ctx.compound_stmt());
        }
        return super.visitStmt(ctx);
    }

    @Override
    public ASTNode visitSimple_stmt(Python3Parser.Simple_stmtContext ctx) {
        System.out.println("Simple_stmtContext: " + ctx.getText());

        if (ctx.expr_stmt() != null) {
            System.out.println("expr_stmtContext: " + ctx.expr_stmt().getText());
            return visit(ctx.expr_stmt());
        } else if (ctx.del_stmt() != null) {
            return visit(ctx.del_stmt());
        } else if (ctx.pass_stmt() != null) {
            return visit(ctx.pass_stmt());
        } else if (ctx.flow_stmt() != null) {
            System.out.println("flow_stmtContext: " + ctx.flow_stmt().getText());
            return visit(ctx.flow_stmt());
        } else if (ctx.import_stmt() != null) {
            return visit(ctx.import_stmt());
        } else if (ctx.global_stmt() != null) {
            return visit(ctx.global_stmt());
        } else if (ctx.nonlocal_stmt() != null) {
            return visit(ctx.nonlocal_stmt());
        } else if (ctx.assert_stmt() != null) {
            return visit(ctx.assert_stmt());
        }
        return super.visitSimple_stmt(ctx);
    }

    @Override
    public ASTNode visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {
        // If there's no '=' token, treat it as a regular expression statement
//        if (ctx.ASSIGN().isEmpty()) {
//            return visitChildren(ctx);
//        }

        // For a simple assignment like "x = 10", we might assume:
        //  - ctx.testlist_star_expr(0) -> LHS
        //  - ctx.testlist_star_expr(1) -> RHS
        //  - ctx.ASSIGN(0) -> '=' token
        //
        System.out.println("Expr_stmtContext: " + ctx.getText());
        System.out.println("LHS: " + ctx.testlist_star_expr(0).getText());
        System.out.println("RHS: " + ctx.testlist_star_expr(1).getText());

        ASTNode left  = visit(ctx.testlist_star_expr(0));
        System.out.println("Left: " + left);
        ASTNode right = visit(ctx.testlist_star_expr(1));
        System.out.println("Right: " + right);

        // Check if the RHS is an InfixExpression
        if (right instanceof InfixExpression infix) {
            System.out.println("Found a subexpression with operator: " + infix.getOperator());
        } else {
            System.out.println("No subexpression on the right-hand side.");
        }


        return ASTNodeFactory.createAssignment(ctx.ASSIGN(0).getText(), left, right, ctx);
    }

    @Override
    public ASTNode visitIf_stmt(Python3Parser.If_stmtContext ctx) {
        // Process "if" condition
        ASTNode condition = visit(ctx.test(0));

        // Process "if" body
        Block body = (Block) visit(ctx.block(0));

        // Process "else" body (if available)
        Block elseBody = null;
        if (ctx.ELSE() != null && ctx.block(1) != null) {
            elseBody = (Block) visit(ctx.block(1));
        }

        // Create an IfStatement using the factory
        return ASTNodeFactory.createIfStatement(condition, body, elseBody, ctx);
    }

    @Override
    public ASTNode visitReturn_stmt(Python3Parser.Return_stmtContext ctx) {
        // Process the return expression (can be null)
        System.out.println("Return_stmtContext: " + ctx.getText());
        ASTNode expression = ctx.testlist() != null ? visit(ctx.testlist()) : null;
        System.out.println("Expression: " + expression);

        // Create a ReturnStatement using the factory
        return ASTNodeFactory.createReturnStatement(expression, ctx);
    }


    @Override
    public ASTNode visitFor_stmt(Python3Parser.For_stmtContext ctx) {
        // Handle for loop components: loop variables, body, and optional else block
        List<ASTNode> loopVariables = new ArrayList<>();
        for (Python3Parser.ExprContext varCtx : ctx.exprlist().expr()) {
            ASTNode variable = visit(varCtx);
            if (variable != null) loopVariables.add(variable);
        }

        Block loopBody = (Block) visit(ctx.block(0));
        Block elseBody = ctx.block().size() > 1 ? (Block) visit(ctx.block(1)) : null;

        // Create the ForStatement node
        return ASTNodeFactory.createForStatement(loopVariables, loopBody, elseBody, ctx);
    }

//    @Override
//    public ASTNode visitWhile_stmt(Python3Parser.While_stmtContext ctx) {
//        // Process the condition
//        ASTNode condition = visit(ctx.test());
//
//        // Process the loop body
//        ASTNode bodyNode = visit(ctx.block());
//        if (!(bodyNode instanceof Block)) {
//            throw new IllegalStateException("Visitor did not return a Block for ctx.block()");
//        }
//        Block body = (Block) bodyNode;
//
//
//        // Create a WhileStatement using the factory
//        return ASTNodeFactory.createWhileStatement(condition, body, ctx);
//    }

    @Override
    public ASTNode visitBlock(Python3Parser.BlockContext ctx) {
        // Create a Block object
        Block block = ASTNodeFactory.createBlock(ctx, new ArrayList<>());
        // Visit and add each statement in this block
        for (Python3Parser.StmtContext statementCtx : ctx.stmt()) {
            System.out.println("StatementContext: " + statementCtx.getText());

            ASTNode statement = visit(statementCtx);
            if (statement == null) { System.out.println("Statement is null"); }
            block.addStatement(statement);
        }

        return block;
    }


    @Override
    public ASTNode visitAtom(Python3Parser.AtomContext ctx) {
        // Handle identifiers or literals
        return ASTNodeFactory.createIdentifier(ctx.getText(), ctx);
//        if (ctx.name() != null) {
//            return ASTNodeFactory.createIdentifier(ctx.name().getText(), ctx);
//        }
//        if (ctx.NUMBER() != null) {
//            return ASTNodeFactory.createIdentifier(ctx.NUMBER().getText(), ctx);
//        }
//        if (ctx.STRING() != null) {
//            return ASTNodeFactory.createIdentifier(ctx.STRING().name(), ctx);
//        }
    }

    private Block toBlock(ASTNode node) {
        /*
         * Helper to safely cast an ASTNode to a Block. If node is null or not a Block,
         * create an empty Block instead. This helps to handle Python’s indentation blocks.
         */
        if (node instanceof Block) {
            return (Block) node;
        }
        // Fallback: create an empty block or handle error as needed
        return ASTNodeFactory.createBlock(null, new ArrayList<>());
    }



//    @Override
//    public ASTNode visitExpr(Python3Parser.ExprContext ctx) {
//        // Handle assignments or infix expressions
//        if (ctx.ASSIGN() != null && ctx.test().size() == 2) {
//            ASTNode left = visit(ctx.test(0));  // LHS
//            ASTNode right = visit(ctx.test(1)); // RHS
//            return ASTNodeFactory.createAssignment(left, right, ctx);
//        } else if (ctx.comp_op() != null && ctx.test().size() == 2) {
//            ASTNode left = visit(ctx.test(0));
//            ASTNode right = visit(ctx.test(1));
//            return ASTNodeFactory.createInfixExpression(left, ctx.comp_op().getText(), right, ctx);
//        }
//        return super.visitExpr(ctx); // Fall back to parent handling
//    }

    
//    @Override
//    public ASTNode visitLambdef(Python3Parser.LambdefContext ctx) {
//        LambdaExpression lambdaExpression = new LambdaExpression();
//
//        if (ctx.varargslist() != null) {
//            for (Python3Parser.VarargsContext paramCtx : ctx.varargslist().vfpdef()) {
//                Parameter param = new Parameter(paramCtx.getText());
//                lambdaExpression.addParameter(param);
//            }
//        }
//
//        ASTNode body = visit(ctx.test());
//        lambdaExpression.setBody(body);
//
//        return lambdaExpression;
//    }
//
//    @Override
//    public ASTNode visitWith_stmt(Python3Parser.With_stmtContext ctx) {
//        WithStatement withStatement = new WithStatement();
//
//        for (Python3Parser.With_itemContext itemCtx : ctx.with_item()) {
//            ContextItem contextItem = new ContextItem();
//            contextItem.setExpression(visit(itemCtx.test()));
//            if (itemCtx.optional_vars() != null) {
//                contextItem.setOptionalVars(visit(itemCtx.optional_vars()));
//            }
//            withStatement.addContextItem(contextItem);
//        }
//
//        Block block = new Block();
//        for (Python3Parser.StmtContext stmtCtx : ctx.stmt()) {
//            ASTNode stmtNode = visit(stmtCtx);
//            block.addStatement(stmtNode);
//        }
//        withStatement.setBlock(block);
//
//        return withStatement;
//    }


}
