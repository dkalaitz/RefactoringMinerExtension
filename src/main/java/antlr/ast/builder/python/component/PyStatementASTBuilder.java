package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PyASTBuilder;
import antlr.ast.node.LangASTNode;
import antlr.ast.node.LangASTNodeFactory;
import antlr.ast.node.PositionUtils;
import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.expression.LangSimpleName;
import antlr.ast.node.literal.LangTupleLiteral;
import antlr.ast.node.metadata.LangAnnotation;
import antlr.ast.node.statement.LangBlock;
import antlr.ast.node.statement.LangCaseStatement;
import antlr.ast.node.statement.LangCatchClause;
import antlr.ast.node.statement.LangWithContextItem;
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
            if (statement != null) {
                langBlock.addStatement(statement);
            } else {
                System.err.println("Warning: null statement encountered in block at " + statementCtx.getText());
            }
        }

        return langBlock;
    }


    public LangASTNode visitStmt(Python3Parser.StmtContext ctx) {
        if (ctx.simple_stmts() != null && !ctx.simple_stmts().isEmpty()) {
            // If there's only one simple statement and it's an import
            if (ctx.simple_stmts().simple_stmt().size() == 1 &&
                    ctx.simple_stmts().simple_stmt(0).import_stmt() != null) {
                // Directly return the import statement without wrapping in a block
                return mainBuilder.visit(ctx.simple_stmts().simple_stmt(0).import_stmt());
            }

            List<LangASTNode> statements = new ArrayList<>();
            for (Python3Parser.Simple_stmtContext simpleStmtContext : ctx.simple_stmts().simple_stmt()) {
                LangASTNode stmtNode = mainBuilder.visit(simpleStmtContext);
                if (stmtNode != null) {
                    if (stmtNode instanceof LangBlock) {
                        // Flatten nested blocks
                        statements.addAll(((LangBlock) stmtNode).getStatements());
                    } else {
                        statements.add(stmtNode);
                    }
                }
            }
            if (statements.size() == 1) {
                // Return the statement directly (unless you always want a block)
                return statements.get(0);
            } else {
                return LangASTNodeFactory.createBlock(ctx, statements);
            }
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

        LangASTNode result = super.mainBuilder.visitSimple_stmt(ctx);
        if (result == null) {
            System.err.println("Unhandled simple statement type: " + ctx.getText());
        }

        return result;
    }



    public LangASTNode visitIf_stmt(Python3Parser.If_stmtContext ctx) {
        // Process the primary "if" condition
        LangASTNode condition = mainBuilder.visit(ctx.test(0));

        // Process the "if" body
        LangBlock body = (LangBlock) mainBuilder.visit(ctx.block(0));

        // Handle the case of `elif` and `else` blocks
        LangASTNode elseBody = null;

        // Check for an `else` clause
        if (ctx.ELSE() != null) {
            int elseBlockIndex = ctx.block().size() - 1;
            elseBody = mainBuilder.visit(ctx.block(elseBlockIndex));
        }

        // Process `elif` clauses (if any), working backwards
        if (ctx.ELIF() != null && !ctx.ELIF().isEmpty()) {
            // Start from the last `elif` condition and chain them backward
            for (int i = ctx.ELIF().size() - 1; i >= 0; i--) {
                LangASTNode elifCondition = mainBuilder.visit(ctx.test(i + 1));
                LangBlock elifBody = (LangBlock) mainBuilder.visit(ctx.block(i + 1));

                // Nest the current `elseBody` into the newly created `IfStatement`
                elseBody = LangASTNodeFactory.createIfStatement(elifCondition, elifBody, elseBody, ctx);
            }
        }

        // Create the top-level `if` statement
        return LangASTNodeFactory.createIfStatement(condition, body, elseBody, ctx);
    }

    public LangASTNode visitTest(Python3Parser.TestContext ctx) {
        if (ctx.or_test() != null) {
            return mainBuilder.visit(ctx.or_test(0));
        }
        return mainBuilder.visitChildren(ctx);
    }

    
    public LangASTNode visitReturn_stmt(Python3Parser.Return_stmtContext ctx) {
        LangASTNode expression = ctx.testlist() != null ? mainBuilder.visit(ctx.testlist()) : null;

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

    public LangASTNode visitBreak_stmt(Python3Parser.Break_stmtContext ctx) {
        return LangASTNodeFactory.createBreakStatement(ctx);
    }

    public LangASTNode visitContinue_stmt(Python3Parser.Continue_stmtContext ctx) {
        return LangASTNodeFactory.createContinueStatement(ctx);
    }

    public LangASTNode visitGlobal_stmt(Python3Parser.Global_stmtContext ctx) {
        // Collect variable names
        List<String> variableNames = new ArrayList<>();
        for (Python3Parser.NameContext nameCtx : ctx.name()) {
            variableNames.add(nameCtx.getText());
        }
        return LangASTNodeFactory.createGlobalStatement(ctx, variableNames);
    }

    public LangASTNode visitPass_stmt(Python3Parser.Pass_stmtContext ctx) {
        return LangASTNodeFactory.createPassStatement(ctx);
    }

    public LangASTNode visitDel_stmt(Python3Parser.Del_stmtContext ctx) {
        List<LangASTNode> targets = new ArrayList<>();
        // Parse all targets in the del statement: del x, y, z...
        if (ctx.exprlist() != null) {
            for (Python3Parser.ExprContext exprCtx : ctx.exprlist().expr()) {
                LangASTNode target = mainBuilder.visit(exprCtx);
                if (target != null) {
                    targets.add(target);
                }
            }
        }
        return LangASTNodeFactory.createDelStatement(ctx, targets);
    }


    public LangASTNode visitYield_stmt(Python3Parser.Yield_stmtContext ctx) {
        LangASTNode expression = null;
        // Check if there is a yield expression: `yield [expression]`
        if (ctx.yield_expr() != null && ctx.yield_expr().yield_arg() != null) {
            // yield_arg can contain a testlist (expression(s))
            if (ctx.yield_expr().yield_arg().testlist() != null) {
                expression = mainBuilder.visit(ctx.yield_expr().yield_arg().testlist());
            }
        }
        return LangASTNodeFactory.createYieldStatement(ctx, expression);
    }


    public LangASTNode visitAssert_stmt(Python3Parser.Assert_stmtContext ctx) {
        LangASTNode expression = mainBuilder.visit(ctx.test(0));
        LangASTNode message = null;

        // Check if there's an optional message (after comma)
        if (ctx.test().size() > 1) {
            message = mainBuilder.visit(ctx.test(1));
        }

        return LangASTNodeFactory.createAssertStatement(ctx, expression, message);
    }

    public LangASTNode visitTry_stmt(Python3Parser.Try_stmtContext ctx) {
        // The first block is always the 'try' block
        List<Python3Parser.BlockContext> blockContexts = ctx.block();

        if (blockContexts.isEmpty()) {
            throw new IllegalStateException("Try statement must have at least one block (the try block)");
        }

        LangBlock tryBlock = (LangBlock) mainBuilder.visit(blockContexts.get(0));
        List<LangCatchClause> catchClauses = new ArrayList<>();
        LangBlock elseBlock = null;
        LangBlock finallyBlock = null;

        List<Python3Parser.Except_clauseContext> exceptClauses = ctx.except_clause();
        int exceptCount = exceptClauses.size();

        if (exceptCount > 0 && blockContexts.size() < 1 + exceptCount) {
            // WORKAROUND: Since the grammar isn't providing except clause bodies as blocks,
            // we create empty catch clauses to prevent crashes. The AST will be incomplete
            // but the application won't crash.

            for (int i = 0; i < exceptCount; i++) {
                LangCatchClause catchClause = (LangCatchClause) mainBuilder.visit(exceptClauses.get(i));

                // Create an empty block with proper position info from the except clause
                List<LangASTNode> emptyStatements = new ArrayList<>();
                LangBlock emptyBlock = LangASTNodeFactory.createBlock(
                        exceptClauses.get(i), // Use the except clause context for position
                        emptyStatements
                );

                catchClause.setBody(emptyBlock);
                catchClauses.add(catchClause);
            }


        } else {
            int currentBlockIndex = 1; // Start after try block

            // Process except clauses
            for (int i = 0; i < exceptCount; i++) {
                if (currentBlockIndex < blockContexts.size()) {
                    LangCatchClause catchClause = (LangCatchClause) mainBuilder.visit(exceptClauses.get(i));
                    LangBlock exceptBody = (LangBlock) mainBuilder.visit(blockContexts.get(currentBlockIndex));
                    catchClause.setBody(exceptBody);
                    catchClauses.add(catchClause);
                    currentBlockIndex++;
                }
            }

            // Process else block
            if (ctx.ELSE() != null && currentBlockIndex < blockContexts.size()) {
                elseBlock = (LangBlock) mainBuilder.visit(blockContexts.get(currentBlockIndex));
                currentBlockIndex++;
            }

            // Process finally block
            if (ctx.FINALLY() != null && currentBlockIndex < blockContexts.size()) {
                finallyBlock = (LangBlock) mainBuilder.visit(blockContexts.get(currentBlockIndex));
            }
        }

        return LangASTNodeFactory.createTryStatement(
                PositionUtils.getPositionInfo(ctx),
                tryBlock,
                catchClauses,
                elseBlock,
                finallyBlock
        );
    }

    // except_clause visitor stays focused - doesn't deal with body
    public LangASTNode visitExcept_clause(Python3Parser.Except_clauseContext ctx) {
        List<LangASTNode> exceptionTypes = new ArrayList<>();
        LangSimpleName exceptionVariable = null;

        if (ctx.test() != null) {
            LangASTNode exceptionType = mainBuilder.visit(ctx.test());
            if (exceptionType instanceof LangTupleLiteral) {
                exceptionTypes.addAll(((LangTupleLiteral) exceptionType).getElements());
            } else {
                exceptionTypes.add(exceptionType);
            }
        }
        if (ctx.name() != null) {
            exceptionVariable = LangASTNodeFactory.createSimpleName(ctx.name().getText(), ctx.name());
        }
        // Don't pass body here!
        return LangASTNodeFactory.createCatchClause(ctx, exceptionTypes, exceptionVariable, null);
    }

    // TODO
    public LangASTNode visitRaise_stmt(Python3Parser.Raise_stmtContext ctx) {
        List<Python3Parser.TestContext> testContexts = ctx.test();
        LangASTNode exception = null;
        LangASTNode from = null;

        if (testContexts != null && !testContexts.isEmpty()) {
            exception = mainBuilder.visit(testContexts.get(0));
        }

        if (ctx.FROM() != null && testContexts.size() >= 2) {
            from = mainBuilder.visit(testContexts.get(1));
        }


        return LangASTNodeFactory.createThrowStatement(ctx, exception, from);
    }


    public LangASTNode visitWith_stmt(Python3Parser.With_stmtContext ctx) {
        // with (with_item (',' with_item)*) block
        List<LangASTNode> contextItems = new ArrayList<>();
        for (Python3Parser.With_itemContext itemCtx : ctx.with_item()) {
            contextItems.add(mainBuilder.visit(itemCtx));
        }
        LangBlock body = (LangBlock) mainBuilder.visit(ctx.block());
        return LangASTNodeFactory.createWithStatement(ctx, contextItems, body);
    }

    public LangWithContextItem visitWith_item(Python3Parser.With_itemContext ctx) {
        // Get the context expression (the thing after 'with')
        LangASTNode contextExpression = mainBuilder.visit(ctx.test());

        LangWithContextItem contextItem = LangASTNodeFactory.createWithContextItem(ctx, contextExpression);

        // Handle optional 'as' clause
        if (ctx.expr() != null) {
            LangASTNode asExpression = mainBuilder.visit(ctx.expr());

            contextItem.setAlias(asExpression);
        }

        return contextItem;
    }

    public LangASTNode visitNonlocal_stmt(Python3Parser.Nonlocal_stmtContext ctx) {
        List<String> variableNames = new ArrayList<>();
        for (Python3Parser.NameContext nameCtx : ctx.name()) {
            variableNames.add(nameCtx.getText());
        }
        return LangASTNodeFactory.createNonlocalStatement(ctx, variableNames);
    }

    public LangASTNode visitAsync_stmt(Python3Parser.Async_stmtContext ctx) {
        LangASTNode body = null;

        // Try to detect which async form it is
        if (ctx.funcdef() != null) {
            // For async functions, we want to return the modified function directly
            body = mainBuilder.visit(ctx.funcdef());

            if (body instanceof LangMethodDeclaration methodDeclaration) {
                // Mark the method as async by adding an annotation
                LangAnnotation asyncAnnotation = LangASTNodeFactory.createAnnotation(
                        ctx,
                        LangASTNodeFactory.createSimpleName("async", ctx),
                        new ArrayList<>()
                );

                List<LangAnnotation> annotations = methodDeclaration.getLangAnnotations();
                if (annotations == null) {
                    annotations = new ArrayList<>();
                }
                annotations.add(asyncAnnotation);
                methodDeclaration.setLangAnnotations(annotations);
                methodDeclaration.setAsync(true);

                String currentSignature = methodDeclaration.getActualSignature();
                if (currentSignature != null && !currentSignature.startsWith("async ")) {
                    methodDeclaration.setActualSignature("async " + currentSignature);
                }
                return methodDeclaration;
            }
        } else if (ctx.with_stmt() != null) {
            body = mainBuilder.visit(ctx.with_stmt());
        } else if (ctx.for_stmt() != null) {
            body = mainBuilder.visit(ctx.for_stmt());
        }

        // For non-function async statements, wrap them in AsyncStatement
        return LangASTNodeFactory.createAsyncStatement(ctx, body);
    }

    public LangASTNode visitLambdadef(Python3Parser.LambdefContext ctx) {
        // 1. Visit parameter list (if any)
        List<LangASTNode> parameters = new ArrayList<>();
        if (ctx.varargslist() != null) {
            for (Python3Parser.VfpdefContext vfpdef : ctx.varargslist().vfpdef()){
                parameters.add(mainBuilder.visit(vfpdef));
            }
        }
        // 2. Visit the expression after the colon
        LangASTNode body = mainBuilder.visit(ctx.test());

        return LangASTNodeFactory.createLambdaExpression(ctx, body, parameters);
    }

    public LangASTNode visitVfpdef(Python3Parser.VfpdefContext ctx) {
        LangSingleVariableDeclaration singleVariableDeclaration = LangASTNodeFactory.createSingleVariableDeclaration(ctx.getText(), ctx);
        singleVariableDeclaration.setParameter(true);
        return singleVariableDeclaration;
    }

    public LangASTNode visitMatch_stmt(Python3Parser.Match_stmtContext ctx) {
        // 1. Get the subject expression (the value being matched)
        LangASTNode subject = mainBuilder.visit(ctx.subject_expr());

        // 2. Prepare a list to collect all case statements
        List<LangCaseStatement> caseStatements = new ArrayList<>();

        // 3. For each case block in the match statement
        for (Python3Parser.Case_blockContext caseCtx : ctx.case_block()) {
            // No pattern handling for now, set pattern to null
            LangASTNode pattern = null;

            // Collect all statements in the body (as a LangBlock, good!)
            LangBlock body = (LangBlock) mainBuilder.visit(caseCtx.block());

            // Create the case statement node without pattern
            LangCaseStatement caseStatement = LangASTNodeFactory.createCaseStatement(ctx, pattern, body);

             caseStatements.add(caseStatement);
        }

        // 4. Create the match/switch node with all case statements
        return LangASTNodeFactory.createSwitchStatement(ctx, subject, caseStatements);
    }
}
