package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PyASTBuilder;
import antlr.ast.node.LangASTNode;
import antlr.ast.node.LangASTNodeFactory;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.statement.LangBlock;
import antlr.base.lang.python.Python3Parser;

import java.util.ArrayList;
import java.util.List;

public class PyStatementASTBuilder extends PyBaseASTBuilder {

    public PyStatementASTBuilder(PyASTBuilder mainBuilder) {
        super(mainBuilder);
    }
    
    public LangASTNode visitBlock(Python3Parser.BlockContext ctx) {
        // Create a LangBlock object
        LangBlock langBlock = LangASTNodeFactory.createBlock(ctx, new ArrayList<>());
        // Visit and add each statement in this langBlock
        for (Python3Parser.StmtContext statementCtx : ctx.stmt()) {
            LangASTNode statement = mainBuilder.visit(statementCtx);
            if (statement == null) { System.out.println("Statement is null"); }
            langBlock.addStatement(statement);
        }

        return langBlock;
    }

    
    public LangASTNode visitStmt(Python3Parser.StmtContext ctx) {
        if (ctx.simple_stmts() != null && !ctx.simple_stmts().isEmpty()) {
            // Use an ArrayList to gather multiple simple statements
            LangBlock statementNodes = LangASTNodeFactory.createBlock(ctx, new ArrayList<>());
            for (Python3Parser.Simple_stmtContext simpleStmtContext : ctx.simple_stmts().simple_stmt()) {
                LangASTNode stmtNode = mainBuilder.visit(simpleStmtContext);
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


    public LangASTNode visitSimple_stmt(Python3Parser.Simple_stmtContext ctx) {

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


    
    public LangASTNode visitIf_stmt(Python3Parser.If_stmtContext ctx) {
        // Process "if" condition
        LangASTNode condition = mainBuilder.visit(ctx.test(0));

        // Process "if" body
        LangBlock body = (LangBlock) mainBuilder.visit(ctx.block(0));

        // Process "else" body (if available)
        LangBlock elseBody = null;
        if (ctx.ELSE() != null && ctx.block(1) != null) {
            elseBody = (LangBlock) mainBuilder.visit(ctx.block(1));
        }

        // Create an LangIfStatement using the factory
        return LangASTNodeFactory.createIfStatement(condition, body, elseBody, ctx);
    }

    public LangASTNode visitTest(Python3Parser.TestContext ctx) {
        if (ctx.or_test() != null) {
            return mainBuilder.visit(ctx.or_test(0));
        }
        // Handle other cases...
        return mainBuilder.visitChildren(ctx);
    }

    
    public LangASTNode visitReturn_stmt(Python3Parser.Return_stmtContext ctx) {
        LangASTNode expression = ctx.testlist() != null ? mainBuilder.visit(ctx.testlist()) : null;
        System.out.println("==== Return_stmt positions ====");
        System.out.println("Start token: " + ctx.getStart().getText());
        System.out.println("Start line: " + ctx.getStart().getLine());
        System.out.println("Start char pos in line: " + ctx.getStart().getCharPositionInLine());
        System.out.println("Start char absolute pos: " + ctx.getStart().getStartIndex());
        System.out.println("Stop token: " + ctx.getStop().getText());
        System.out.println("Stop line: " + ctx.getStop().getLine());
        System.out.println("Stop char pos in line: " + ctx.getStop().getCharPositionInLine());
        System.out.println("Stop char absolute pos: " + ctx.getStop().getStopIndex());
        System.out.println("========================");

        // Create a LangReturnStatement using the factory
        return LangASTNodeFactory.createReturnStatement(expression, ctx);
    }



    public LangASTNode visitFor_stmt(Python3Parser.For_stmtContext ctx) {
        // Handle initialization variables (Python for loops have loop variables in exprlist)
        List<LangSingleVariableDeclaration> initializers = new ArrayList<>();
        for (Python3Parser.ExprContext varCtx : ctx.exprlist().expr()) {
                LangSingleVariableDeclaration declaration = LangASTNodeFactory.createSingleVariableDeclaration(
                        varCtx.getText(),
                        varCtx
                );
                initializers.add(declaration);
        }

        // Python for-loop condition is derived from the iterable expression in `testlist`
        LangASTNode condition = mainBuilder.visit(ctx.testlist());

        // Updates are typically not part of Python's for-loops, but can be left empty
        List<LangASTNode> updates = new ArrayList<>();

        // Visit the loop body (mandatory) and the else body (optional)
        LangBlock loopBody = (LangBlock) mainBuilder.visit(ctx.block(0));
        LangBlock elseBody = ctx.block().size() > 1 ? (LangBlock) mainBuilder.visit(ctx.block(1)) : null;

        // Create the LangForStatement node
        return LangASTNodeFactory.createForStatement(initializers, condition, updates, loopBody, elseBody, ctx);
    }

    public LangASTNode visitWhile_stmt(Python3Parser.While_stmtContext ctx) {
        // Parse the condition
        LangASTNode condition = mainBuilder.visit(ctx.test());

        // Visit the main while body
        LangBlock body = (LangBlock) mainBuilder.visit(ctx.block(0));

        LangBlock elseBody = null;

        if (ctx.block().size() > 1) {
            elseBody = (LangBlock) mainBuilder.visit(ctx.block(1));
        }

        return LangASTNodeFactory.createWhileStatement(condition, body, elseBody, ctx);
    }

    //
//    
//    public LangASTNode visitWith_stmt(Python3Parser.With_stmtContext ctx) {
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
//        LangBlock block = new LangBlock();
//        for (Python3Parser.StmtContext stmtCtx : ctx.stmt()) {
//            LangASTNode stmtNode = visit(stmtCtx);
//            block.addStatement(stmtNode);
//        }
//        withStatement.setBlock(block);
//
//        return withStatement;
//    }

}
