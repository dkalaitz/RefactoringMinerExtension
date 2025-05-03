package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PyASTBuilder;
import antlr.ast.builder.python.PyASTBuilderUtil;
import antlr.ast.node.LangASTNode;
import antlr.ast.node.LangASTNodeFactory;
import antlr.ast.node.expression.LangAssignment;
import antlr.ast.node.expression.LangMethodInvocation;
import antlr.ast.node.expression.LangSimpleName;
import antlr.ast.node.literal.LangDictionaryLiteral;
import antlr.ast.node.statement.LangExpressionStatement;
import antlr.base.lang.python.Python3Parser;

import java.util.ArrayList;
import java.util.List;

import static antlr.ast.node.LangASTNodeFactory.createExpressionStatement;

public class PyExpressionASTBuilder extends PyBaseASTBuilder {

    public PyExpressionASTBuilder(PyASTBuilder mainBuilder) {
        super(mainBuilder);
    }

    
    public LangASTNode visitAtom(Python3Parser.AtomContext ctx) {
        // Handle identifiers or literals
        if (ctx.NUMBER() != null) {
            return LangASTNodeFactory.createIntegerLiteral(ctx, ctx.NUMBER().getText());
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

    // TODO: might not be needed
    public LangASTNode visitTestlist_star_expr(Python3Parser.Testlist_star_exprContext ctx) {

        return mainBuilder.visit(ctx.test(0));
    }

    public LangASTNode visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {

        // Case 1: Simple expression without assignment
        if (ctx.ASSIGN().isEmpty() && ctx.augassign() == null) {
            LangASTNode expr = mainBuilder.visit(ctx.testlist_star_expr(0));
            // Create an expression statement to wrap the expression
            return LangASTNodeFactory.createExpressionStatement(expr, ctx);
        }

        // For a simple assignment like "x = 10"
        // TODO: Handle more complex assignments
        LangASTNode left = mainBuilder.visit(ctx.testlist_star_expr(0));
        LangASTNode right = mainBuilder.visit(ctx.testlist_star_expr(1));

        // Create the assignment with correct source position
        LangAssignment assignment = LangASTNodeFactory.createAssignment(ctx.ASSIGN(0).getText(), left, right, ctx);

        // Wrap in expression statement with correct source position
        return LangASTNodeFactory.createExpressionStatement(assignment, ctx);
    }


    // TODO: TO UNCOMMENT
    
//    public LangASTNode visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {
//
//        // Case 1: Simple expression without assignment
//        if (ctx.ASSIGN().isEmpty() && ctx.augassign() == null) {
//            return mainBuilder.visit(ctx.testlist_star_expr(0));
//        }
//
//        // TODO handle more complex assignments
////        // Case 2: Augmented assignment (+=, -=, etc.)
////        if (ctx.augassign() != null) {
////            LangASTNode left = visit(ctx.testlist_star_expr(0));
////            LangASTNode right = visit(ctx.test(0));
////            String operator = ctx.augassign().getText();
////
////            return LangASTNodeFactory.createAugmentedAssignment(left, right, operator, ctx);
////        }
//
//
//        // For a simple assignment like "x = 10", we might assume:
//        //  - ctx.testlist_star_expr(0) -> LHS
//        //  - ctx.testlist_star_expr(1) -> RHS
//        //  - ctx.ASSIGN(0) -> '=' token
//        //
//        LangASTNode left  = mainBuilder.visit(ctx.testlist_star_expr(0));
//        LangASTNode right = mainBuilder.visit(ctx.testlist_star_expr(1));
//
//        LangAssignment langAssignment = LangASTNodeFactory.createAssignment(ctx.ASSIGN(0).getText(), left, right, ctx);
//
//        return LangASTNodeFactory.createExpressionStatement(langAssignment, ctx);
//
//    }

    
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


    //TODO: Uncomment
//    public LangASTNode visitTrailer(Python3Parser.TrailerContext ctx) {
//        if (ctx.OPEN_PAREN() != null) {
//            LangMethodInvocation langMethodInvocation = LangASTNodeFactory.createMethodInvocation(ctx);
//
//            if (ctx.arglist() != null) {
//                for (Python3Parser.ArgumentContext argCtx : ctx.arglist().argument()) {
//                    // Visit each argument and add it to the method invocation
//                    LangASTNode argNode = mainBuilder.visit(argCtx);
//                    if (argNode != null) {
//                        langMethodInvocation.addArgument(argNode);
//                    }
//                }
//            }
//            return langMethodInvocation;
//        }
//        return null;
//    }


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
