package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PyASTBuilder;
import antlr.ast.builder.python.PyASTBuilderUtil;
import antlr.ast.node.LangASTNode;
import antlr.ast.node.LangASTNodeFactory;
import antlr.ast.node.expression.LangMethodInvocation;
import antlr.ast.node.misc.LangSimpleName;
import antlr.base.python.Python3Parser;

public class PyExpressionASTBuilderPy extends PyBaseASTBuilder {

    public PyExpressionASTBuilderPy(PyASTBuilder mainBuilder) {
        super(mainBuilder);
    }

    
    public LangASTNode visitAtom(Python3Parser.AtomContext ctx) {
        // Handle identifiers or literals
        if (ctx.NUMBER() != null) {
            return LangASTNodeFactory.createIntegerLiteral(ctx, ctx.NUMBER().getText());
        }
        // TODO
//        if (ctx.STRING() != null) {
//            return LangASTNodeFactory.createStringLiteral(ctx, ctx.getText());
//        }
        if (ctx.TRUE() != null || ctx.FALSE() != null) {
            return LangASTNodeFactory.createBooleanLiteral(ctx, Boolean.parseBoolean(ctx.getText()));
        }
        return LangASTNodeFactory.createSimpleName(ctx.getText(), ctx);

    }

    public LangASTNode visitAtom_expr(Python3Parser.Atom_exprContext ctx) {
        // Handle the base atom (which could be a function name like "print")
        LangASTNode baseExpr = mainBuilder.visit(ctx.atom());

        // If there are no trailers, just return the atom
        if (ctx.trailer().isEmpty()) {
            return baseExpr;
        }

        // Process trailers (method calls, attribute access, etc.)
        LangASTNode result = baseExpr;
        for (Python3Parser.TrailerContext trailerCtx : ctx.trailer()) {
            if (trailerCtx.OPEN_PAREN() != null) {
                // This is a function call like print(i) or a method call like obj.method()

                // Get the name - either from the base expression or from a previous dot access
                String methodName;
                LangASTNode expression = null;

                if (baseExpr instanceof LangSimpleName) {
                    // For simple function calls like print(i)
                    methodName = ((LangSimpleName) baseExpr).getIdentifier();
                    // The function name itself is the expression
                    expression = baseExpr;
                } else {
                    // This will need to handle more complex cases
                    methodName = ""; // Will be set appropriately for method calls
                    expression = result; // The current result becomes the expression
                }

                // Create the method invocation with correct position
                int startLine = ctx.getStart().getLine();
                int startChar = ctx.getStart().getCharPositionInLine();
                int endLine = trailerCtx.getStop().getLine();
                int endChar = trailerCtx.getStop().getCharPositionInLine() + trailerCtx.getStop().getText().length();

                LangMethodInvocation langMethodInvocation = LangASTNodeFactory.createMethodInvocation(
                        ctx.getParent() // Use parent context for proper span
                );

                // Set the expression - this is the object on which the method is called
                // For function calls like print(), this will be the LangSimpleName "print"
                langMethodInvocation.setExpression(expression);

                // Process arguments if they exist
                if (trailerCtx.arglist() != null) {
                    for (Python3Parser.ArgumentContext argCtx : trailerCtx.arglist().argument()) {
                        // Visit each argument and add it to the method invocation
                        LangASTNode argNode = mainBuilder.visit(argCtx);
                        if (argNode != null) {
                            langMethodInvocation.addArgument(argNode);
                        }
                    }
                }

                result = langMethodInvocation;
            } else if (trailerCtx.DOT() != null && trailerCtx.name() != null) {
                // This is attribute access: obj.attr
                // Create a property access node
                String attrName = trailerCtx.name().getText();

                // If the next trailer is OPEN_PAREN, this is part of a method call
                // You'll need to handle this based on your AST structure

                // For now, create a LangSimpleName for the attribute
                // You might want a more specific node type for attribute access

                // Update result for further processing
                result = LangASTNodeFactory.createSimpleName(attrName, trailerCtx); // This is simplified - you'll need property access nodes
            }
        }

        return result;
    }

    // TODO: might not be needed
    public LangASTNode visitTestlist_star_expr(Python3Parser.Testlist_star_exprContext ctx) {

        return mainBuilder.visit(ctx.test(0));
    }

    
    public LangASTNode visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {

        // Case 1: Simple expression without assignment
        if (ctx.ASSIGN().isEmpty() && ctx.augassign() == null) {
            return mainBuilder.visit(ctx.testlist_star_expr(0));
        }

        // TODO handle more complex assignments
//        // Case 2: Augmented assignment (+=, -=, etc.)
//        if (ctx.augassign() != null) {
//            LangASTNode left = visit(ctx.testlist_star_expr(0));
//            LangASTNode right = visit(ctx.test(0));
//            String operator = ctx.augassign().getText();
//
//            return LangASTNodeFactory.createAugmentedAssignment(left, right, operator, ctx);
//        }


        // For a simple assignment like "x = 10", we might assume:
        //  - ctx.testlist_star_expr(0) -> LHS
        //  - ctx.testlist_star_expr(1) -> RHS
        //  - ctx.ASSIGN(0) -> '=' token
        //
        LangASTNode left  = mainBuilder.visit(ctx.testlist_star_expr(0));
        LangASTNode right = mainBuilder.visit(ctx.testlist_star_expr(1));

        return LangASTNodeFactory.createAssignment(ctx.ASSIGN(0).getText(), left, right, ctx);
    }

    
    public LangASTNode visitExpr(Python3Parser.ExprContext ctx) {
        // 1. If the rule matched something like an atom_expr
        if (ctx.atom_expr() != null) {
            return mainBuilder.visitAtom_expr(ctx.atom_expr());
        }

        if (ctx.expr().size() == 2) {
            LangASTNode leftNode = mainBuilder.visit(ctx.expr(0));
            LangASTNode rightNode = mainBuilder.visit(ctx.expr(1));
            String operator = PyASTBuilderUtil.extractOperator(ctx);
            return LangASTNodeFactory.createInfixExpression(leftNode, rightNode, operator, ctx);
        }

        // TODO: Handle other binary operators (** , *, /, etc.), or bitwise ops (&, |, ^)

        return mainBuilder.visitChildren(ctx);
    }

    public LangASTNode visitComparison(Python3Parser.ComparisonContext ctx) {
        // If there's only one expr, just visit it
        if (ctx.expr().size() == 1) {
            return mainBuilder.visit(ctx.expr(0));
        }

        // If there are multiple expressions connected by comparison operators
        if (ctx.expr().size() >= 2) {
            LangASTNode leftNode = mainBuilder.visit(ctx.expr(0));

            // For each comparison operator and right operand
            for (int i = 0; i < ctx.comp_op().size(); i++) {
                LangASTNode rightNode = mainBuilder.visit(ctx.expr(i + 1));
                String operator = ctx.comp_op(i).getText();

                // Create an infix expression for this comparison
                leftNode = LangASTNodeFactory.createInfixExpression(leftNode, rightNode, operator, ctx);
            }

            return leftNode;
        }

        return mainBuilder.visitChildren(ctx);
    }

    public LangASTNode visitTrailer(Python3Parser.TrailerContext ctx) {
        if (ctx.OPEN_PAREN() != null) {
            LangMethodInvocation langMethodInvocation = LangASTNodeFactory.createMethodInvocation(ctx);

            if (ctx.arglist() != null) {
                for (Python3Parser.ArgumentContext argCtx : ctx.arglist().argument()) {
                    // Visit each argument and add it to the method invocation
                    LangASTNode argNode = mainBuilder.visit(argCtx);
                    if (argNode != null) {
                        langMethodInvocation.addArgument(argNode);
                    }
                }
            }
            return langMethodInvocation;
        }
        return null;
    }


    //
//    public LangASTNode visitLambdef(Python3Parser.LambdefContext ctx) {
//        LambdaExpression lambdaExpression = new LambdaExpression();
//
//        if (ctx.varargslist() != null) {
//            for (Python3Parser.VarargsContext paramCtx : ctx.varargslist().vfpdef()) {
//                LangSingleVariableDeclaration param = new LangSingleVariableDeclaration(paramCtx.getText());
//                lambdaExpression.addParameter(param);
//            }
//        }
//
//        LangASTNode body = visit(ctx.test());
//        lambdaExpression.setBody(body);
//
//        return lambdaExpression;
//    }

}
