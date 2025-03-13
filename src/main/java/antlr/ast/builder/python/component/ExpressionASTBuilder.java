package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PythonASTBuilder;
import antlr.ast.builder.python.PythonASTBuilderUtil;
import antlr.ast.node.ASTNode;
import antlr.ast.node.ASTNodeFactory;
import antlr.base.python.Python3Parser;

public class ExpressionASTBuilder extends BasePythonASTBuilder {

    public ExpressionASTBuilder(PythonASTBuilder mainBuilder) {
        super(mainBuilder);
    }

    
    public ASTNode visitAtom(Python3Parser.AtomContext ctx) {
        // Handle identifiers or literals
        return ASTNodeFactory.createSimpleName(ctx.getText(), ctx);
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

    // TODO: might not be needed
    public ASTNode visitTestlist_star_expr(Python3Parser.Testlist_star_exprContext ctx) {

        return mainBuilder.visit(ctx.test(0));
    }

    
    public ASTNode visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {

        // Case 1: Simple expression without assignment
        if (ctx.ASSIGN().isEmpty() && ctx.augassign() == null) {
            return mainBuilder.visit(ctx.testlist_star_expr(0));
        }

        // TODO handle more complex assignments
//        // Case 2: Augmented assignment (+=, -=, etc.)
//        if (ctx.augassign() != null) {
//            ASTNode left = visit(ctx.testlist_star_expr(0));
//            ASTNode right = visit(ctx.test(0));
//            String operator = ctx.augassign().getText();
//
//            return ASTNodeFactory.createAugmentedAssignment(left, right, operator, ctx);
//        }


        // For a simple assignment like "x = 10", we might assume:
        //  - ctx.testlist_star_expr(0) -> LHS
        //  - ctx.testlist_star_expr(1) -> RHS
        //  - ctx.ASSIGN(0) -> '=' token
        //
        ASTNode left  = mainBuilder.visit(ctx.testlist_star_expr(0));
        ASTNode right = mainBuilder.visit(ctx.testlist_star_expr(1));

        return ASTNodeFactory.createAssignment(ctx.ASSIGN(0).getText(), left, right, ctx);
    }

    
    public ASTNode visitExpr(Python3Parser.ExprContext ctx) {
        // 1. If the rule matched something like an atom_expr
        if (ctx.atom_expr() != null) {
            return mainBuilder.visitAtom_expr(ctx.atom_expr());
        }

        if (ctx.expr().size() == 2) {
            ASTNode leftNode = mainBuilder.visit(ctx.expr(0));
            ASTNode rightNode = mainBuilder.visit(ctx.expr(1));
            String operator = PythonASTBuilderUtil.extractOperator(ctx);
            return ASTNodeFactory.createInfixExpression(leftNode, rightNode, operator, ctx);
        }

        // TODO: Handle other binary operators (** , *, /, etc.), or bitwise ops (&, |, ^)

        return mainBuilder.visitChildren(ctx);
    }

    public ASTNode visitComparison(Python3Parser.ComparisonContext ctx) {
        // If there's only one expr, just visit it
        if (ctx.expr().size() == 1) {
            return mainBuilder.visit(ctx.expr(0));
        }

        // If there are multiple expressions connected by comparison operators
        if (ctx.expr().size() >= 2) {
            ASTNode leftNode = mainBuilder.visit(ctx.expr(0));

            // For each comparison operator and right operand
            for (int i = 0; i < ctx.comp_op().size(); i++) {
                ASTNode rightNode = mainBuilder.visit(ctx.expr(i + 1));
                String operator = ctx.comp_op(i).getText();

                // Create an infix expression for this comparison
                leftNode = ASTNodeFactory.createInfixExpression(leftNode, rightNode, operator, ctx);
            }

            return leftNode;
        }

        return mainBuilder.visitChildren(ctx);
    }


    
    //    
//    public ASTNode visitLambdef(Python3Parser.LambdefContext ctx) {
//        LambdaExpression lambdaExpression = new LambdaExpression();
//
//        if (ctx.varargslist() != null) {
//            for (Python3Parser.VarargsContext paramCtx : ctx.varargslist().vfpdef()) {
//                SingleVariableDeclaration param = new SingleVariableDeclaration(paramCtx.getText());
//                lambdaExpression.addParameter(param);
//            }
//        }
//
//        ASTNode body = visit(ctx.test());
//        lambdaExpression.setBody(body);
//
//        return lambdaExpression;
//    }

}
