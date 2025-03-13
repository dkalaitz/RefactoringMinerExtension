package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PythonASTBuilder;
import antlr.ast.node.ASTNode;
import antlr.ast.node.ASTNodeFactory;
import antlr.ast.node.misc.SimpleName;
import antlr.ast.node.misc.SingleVariableDeclaration;
import antlr.ast.node.statement.Block;
import antlr.base.python.Python3Parser;

import java.util.ArrayList;
import java.util.List;

public class StatementASTBuilder extends BasePythonASTBuilder {

    public StatementASTBuilder(PythonASTBuilder mainBuilder) {
        super(mainBuilder);
    }
    
    public ASTNode visitBlock(Python3Parser.BlockContext ctx) {
        // Create a Block object
        Block block = ASTNodeFactory.createBlock(ctx, new ArrayList<>());
        // Visit and add each statement in this block
        for (Python3Parser.StmtContext statementCtx : ctx.stmt()) {
            ASTNode statement = mainBuilder.visit(statementCtx);
            if (statement == null) { System.out.println("Statement is null"); }
            block.addStatement(statement);
        }

        return block;
    }

    
    public ASTNode visitStmt(Python3Parser.StmtContext ctx) {
        if (ctx.simple_stmts() != null && !ctx.simple_stmts().isEmpty()) {
            // Use an ArrayList to gather multiple simple statements
            Block statementNodes = ASTNodeFactory.createBlock(ctx, new ArrayList<>());
            for (Python3Parser.Simple_stmtContext simpleStmtContext : ctx.simple_stmts().simple_stmt()) {
                ASTNode stmtNode = mainBuilder.visit(simpleStmtContext);
                if (stmtNode != null) {
                    statementNodes.addStatement(stmtNode);
                }
            }

            return statementNodes;
        }
        else if (ctx.compound_stmt() != null) {
            return mainBuilder.visit(ctx.compound_stmt());
        }
        return super.mainBuilder.visitStmt(ctx);
    }


    public ASTNode visitSimple_stmt(Python3Parser.Simple_stmtContext ctx) {

        if (ctx.expr_stmt() != null) {
            return mainBuilder.visit(ctx.expr_stmt());
        } else if (ctx.del_stmt() != null) {
            return mainBuilder.visit(ctx.del_stmt());
        } else if (ctx.pass_stmt() != null) {
            return mainBuilder.visit(ctx.pass_stmt());
        } else if (ctx.flow_stmt() != null) {
            return mainBuilder.visit(ctx.flow_stmt());
        } else if (ctx.import_stmt() != null) {
            return mainBuilder.visit(ctx.import_stmt());
        } else if (ctx.global_stmt() != null) {
            return mainBuilder.visit(ctx.global_stmt());
        } else if (ctx.nonlocal_stmt() != null) {
            return mainBuilder.visit(ctx.nonlocal_stmt());
        } else if (ctx.assert_stmt() != null) {
            return mainBuilder.visit(ctx.assert_stmt());
        }
        return super.mainBuilder.visitSimple_stmt(ctx);
    }


    
    public ASTNode visitIf_stmt(Python3Parser.If_stmtContext ctx) {
        // Process "if" condition
        ASTNode condition = mainBuilder.visit(ctx.test(0));

        // Process "if" body
        Block body = (Block) mainBuilder.visit(ctx.block(0));

        // Process "else" body (if available)
        Block elseBody = null;
        if (ctx.ELSE() != null && ctx.block(1) != null) {
            elseBody = (Block) mainBuilder.visit(ctx.block(1));
        }

        // Create an IfStatement using the factory
        return ASTNodeFactory.createIfStatement(condition, body, elseBody, ctx);
    }

    public ASTNode visitTest(Python3Parser.TestContext ctx) {
        if (ctx.or_test() != null) {
            return mainBuilder.visit(ctx.or_test(0));
        }
        // Handle other cases...
        return mainBuilder.visitChildren(ctx);
    }

    
    public ASTNode visitReturn_stmt(Python3Parser.Return_stmtContext ctx) {
        ASTNode expression = ctx.testlist() != null ? mainBuilder.visit(ctx.testlist()) : null;

        // Create a ReturnStatement using the factory
        return ASTNodeFactory.createReturnStatement(expression, ctx);
    }



    public ASTNode visitFor_stmt(Python3Parser.For_stmtContext ctx) {
        // Handle initialization variables (Python for loops have loop variables in exprlist)
        List<SingleVariableDeclaration> initializers = new ArrayList<>();
        for (Python3Parser.ExprContext varCtx : ctx.exprlist().expr()) {
                SingleVariableDeclaration declaration = ASTNodeFactory.createSingleVariableDeclaration(
                        varCtx.getText(),
                        varCtx
                );
                initializers.add(declaration);
        }

        // Python for-loop condition is derived from the iterable expression in `testlist`
        ASTNode condition = mainBuilder.visit(ctx.testlist());

        // Updates are typically not part of Python's for-loops, but can be left empty
        List<ASTNode> updates = new ArrayList<>();

        // Visit the loop body (mandatory) and the else body (optional)
        Block loopBody = (Block) mainBuilder.visit(ctx.block(0));
        Block elseBody = ctx.block().size() > 1 ? (Block) mainBuilder.visit(ctx.block(1)) : null;

        // Create the ForStatement node
        return ASTNodeFactory.createForStatement(initializers, condition, updates, loopBody, elseBody, ctx);
    }

    public ASTNode visitWhile_stmt(Python3Parser.While_stmtContext ctx) {
        // Parse the condition
        ASTNode condition = mainBuilder.visit(ctx.test());

        // Visit the main while body
        Block body = (Block) mainBuilder.visit(ctx.block(0));

        Block elseBody = null;

        if (ctx.block().size() > 1) {
            elseBody = (Block) mainBuilder.visit(ctx.block(1));
        }

        return ASTNodeFactory.createWhileStatement(condition, body, elseBody, ctx);
    }

    //
//    
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
