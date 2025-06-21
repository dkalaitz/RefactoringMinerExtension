package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PyASTBuilder;
import antlr.ast.builder.python.PyASTBuilderUtil;
import antlr.ast.node.LangASTNode;
import antlr.ast.node.LangASTNodeFactory;
import antlr.ast.node.OperatorEnum;
import antlr.ast.node.expression.LangAssignment;
import antlr.ast.node.expression.LangMethodInvocation;
import antlr.ast.node.literal.LangDictionaryLiteral;
import antlr.ast.node.literal.LangStringLiteral;
import antlr.base.lang.python.Python3Parser;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;

public class PyExpressionASTBuilder extends PyBaseASTBuilder {

    public PyExpressionASTBuilder(PyASTBuilder mainBuilder) {
        super(mainBuilder);
    }

    public LangASTNode visitAtom(Python3Parser.AtomContext ctx) {
        // Handle identifiers or literals
        if (ctx.NUMBER() != null) {
            return LangASTNodeFactory.createNumberLiteral(ctx, ctx.NUMBER().getText());
        }
        if (ctx.name() != null && ctx.name().getText().equals("None")) {
            return LangASTNodeFactory.createNullLiteral(ctx);
        }
        if (ctx.getText() != null && PyASTBuilderUtil.isStringLiteral(ctx)) {
            return LangASTNodeFactory.createStringLiteral(ctx, ctx.getText());
        }
        if (ctx.TRUE() != null || ctx.FALSE() != null) {
            return LangASTNodeFactory.createBooleanLiteral(ctx, Boolean.parseBoolean(ctx.getText()));
        }
        if (ctx.getText() != null && PyASTBuilderUtil.isListLiteral(ctx)){
            List<LangASTNode> elements = new ArrayList<>();
            // If there's a testlist_comp, it contains the elements
            if (ctx.testlist_comp() != null) {
                for (Python3Parser.TestContext testCtx : ctx.testlist_comp().test()) {
                    elements.add(mainBuilder.visit(testCtx));
                }
            }
            return LangASTNodeFactory.createListLiteral(ctx, elements);
        }
        // Handle tuple literals
        if (ctx.OPEN_PAREN() != null && ctx.CLOSE_PAREN() != null && ctx.testlist_comp() != null) {
            List<LangASTNode> elements = new ArrayList<>();
            for (Python3Parser.TestContext testCtx : ctx.testlist_comp().test()) {
                elements.add(mainBuilder.visit(testCtx));
            }
            return LangASTNodeFactory.createTupleLiteral(ctx, elements);
        }

        // Handle dictionary literals
        if (ctx.OPEN_BRACE() != null && ctx.CLOSE_BRACE() != null && ctx.dictorsetmaker() != null) {
            LangDictionaryLiteral dict = LangASTNodeFactory.createDictionaryLiteral(ctx);

            // Process key-value pairs if they exist
            Python3Parser.DictorsetmakerContext dictCtx = ctx.dictorsetmaker();
            if (dictCtx != null && dictCtx.test().size() % 2 == 0) {
                for (int i = 0; i < dictCtx.test().size(); i += 2) {
                    LangASTNode key = mainBuilder.visit(dictCtx.test(i));
                    LangASTNode value = mainBuilder.visit(dictCtx.test(i + 1));
                    dict.addEntry(key, value);
                }
            }

            return dict;
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

        if (ctx.AWAIT() != null) {
            LangASTNode innerExpr = mainBuilder.visit(ctx.atom());
            return LangASTNodeFactory.createAwaitExpression(ctx, innerExpr);
        }

        // Process trailers (method calls, attribute access, etc.)
        LangASTNode result = baseExpr;
        for (Python3Parser.TrailerContext trailerCtx : ctx.trailer()) {
            if (trailerCtx.OPEN_PAREN() != null) {
                // This is a function call like print(i) or a method call like obj.method()
                LangMethodInvocation methodInvocation = LangASTNodeFactory.createMethodInvocation(ctx.getParent());

                // Set the expression - this is the object on which the method is called
                methodInvocation.setExpression(result);

                // Process arguments if they exist
                if (trailerCtx.arglist() != null) {
                    for (Python3Parser.ArgumentContext argCtx : trailerCtx.arglist().argument()) {
                        // Visit each argument and add it to the method invocation
                        LangASTNode argNode = mainBuilder.visit(argCtx);
                        if (argNode != null) {
                            methodInvocation.addArgument(argNode);
                        }
                    }
                }

                result = methodInvocation;
            } else if (trailerCtx.DOT() != null && trailerCtx.name() != null) {
                // This is attribute access: obj.attr
                String attrName = trailerCtx.name().getText();

                // Create a field access node that combines the object and the field name
                result = LangASTNodeFactory.createFieldAccess(result, attrName, trailerCtx);
            }
        }
        return result;
    }

    public LangASTNode visitStar_expr(Python3Parser.Star_exprContext ctx) {
        LangASTNode expr = mainBuilder.visit(ctx.expr());
        return LangASTNodeFactory.createPrefixExpression(expr, OperatorEnum.fromSymbol("*").getSymbol(), ctx);
    }


    public LangASTNode visitTestlist_star_expr(Python3Parser.Testlist_star_exprContext ctx) {
        // Handle single element case
        if (ctx.test().size() == 1 && ctx.star_expr().isEmpty()) {
            return mainBuilder.visit(ctx.test(0));
        }

        // Handle multiple elements or star expressions - create a list/tuple
        List<LangASTNode> elements = new ArrayList<>();

        // Add regular test expressions
        for (Python3Parser.TestContext testCtx : ctx.test()) {
            elements.add(mainBuilder.visit(testCtx));
        }

        // Add star expressions
        for (Python3Parser.Star_exprContext starCtx : ctx.star_expr()) {
            elements.add(mainBuilder.visit(starCtx));
        }

        if (elements.size() > 1) {
            return LangASTNodeFactory.createTupleLiteral(ctx, elements);
        }

        // Single element
        return elements.get(0);
    }

    public LangASTNode visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {
        // Case 1: Simple expression without assignment
        if (ctx.ASSIGN().isEmpty() && ctx.augassign() == null) {
            LangASTNode expr = mainBuilder.visit(ctx.testlist_star_expr(0));

            // Check if this is a module-level docstring
            if (expr instanceof LangStringLiteral && isModuleLevelDocstring(ctx)) {
                return LangASTNodeFactory.createComment(ctx, ((LangStringLiteral) expr).getValue(), false, true);
            }

            // Create an expression statement to wrap the expression
            return LangASTNodeFactory.createExpressionStatement(expr, ctx);
        }

        // Case 2: Augmented assignment (+=, -=, etc.)
        if (ctx.augassign() != null) {
            LangASTNode left = mainBuilder.visit(ctx.testlist_star_expr(0));
            LangASTNode right = mainBuilder.visit(ctx.testlist().test(0));
            String operator = ctx.augassign().getText();

            // Create the augmented assignment
            LangAssignment assignment = LangASTNodeFactory.createAssignment(operator, left, right, ctx);

            // Wrap in expression statement
            return LangASTNodeFactory.createExpressionStatement(assignment, ctx);
        }

        // Case 3: Regular assignment (=)
        LangASTNode left = mainBuilder.visit(ctx.testlist_star_expr(0));
        LangASTNode right = mainBuilder.visit(ctx.testlist_star_expr(1));

        LangAssignment assignment = LangASTNodeFactory.createAssignment("=", left, right, ctx);

        // Wrap in expression statement
        return LangASTNodeFactory.createExpressionStatement(assignment, ctx);
    }


    private boolean isModuleLevelDocstring(Python3Parser.Expr_stmtContext ctx) {
        ParserRuleContext parent = ctx.getParent();
        while (parent != null) {
            if (parent instanceof Python3Parser.FuncdefContext ||
                    parent instanceof Python3Parser.ClassdefContext) {
                return false; // Inside a function or class
            }
            if (parent instanceof Python3Parser.File_inputContext) {
                return true; // At module level
            }
            parent = parent.getParent();
        }
        return false;
    }



    public LangASTNode visitExpr(Python3Parser.ExprContext ctx) {
        if (ctx.atom_expr() != null) {
            return mainBuilder.visitAtom_expr(ctx.atom_expr());
        }

        if (ctx.expr().size() == 2) {
            LangASTNode leftNode = mainBuilder.visit(ctx.expr(0));
            LangASTNode rightNode = mainBuilder.visit(ctx.expr(1));
            String operator = PyASTBuilderUtil.extractOperator(ctx);
            return LangASTNodeFactory.createInfixExpression(leftNode, rightNode, operator, ctx);
        }

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

    public LangASTNode visitPattern(Python3Parser.PatternContext ctx) {
        // If we have an as_pattern, visit it
        if (ctx.as_pattern() != null) {
            return mainBuilder.visit(ctx.as_pattern());
        }

        //TODO
//        if (ctx.or_pattern() != null) {
//            return mainBuilder.visit(ctx.or_pattern());
//        }

        // Should not occur, but as fallback
        return super.mainBuilder.visitPattern(ctx);
    }

}
