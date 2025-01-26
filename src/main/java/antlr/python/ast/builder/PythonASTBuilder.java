package antlr.python.ast.builder;

import antlr.python.base.Python3Parser;
import antlr.python.base.Python3ParserBaseVisitor;
import antlr.python.node.ASTNode;
import antlr.python.node.child.*;


public class PythonASTBuilder extends Python3ParserBaseVisitor<ASTNode> {

    @Override
    public ASTNode visitFile_input(Python3Parser.File_inputContext ctx) {
        // Create the root CompilationUnit
        CompilationUnit compilationUnit = new CompilationUnit();

        // Visit all statements in the file and add them to the CompilationUnit
        for (Python3Parser.StmtContext stmt : ctx.stmt()) {
            ASTNode node = visit(stmt);
            if (node instanceof TypeDeclaration) {
                compilationUnit.addType((TypeDeclaration) node);
            }
        }

        return compilationUnit;
    }

    @Override
    public ASTNode visitClassdef(Python3Parser.ClassdefContext ctx) {
        // Create a TypeDeclaration for the class
        TypeDeclaration typeDeclaration = new TypeDeclaration();
        typeDeclaration.setName(ctx.name().getText()); // Class name

        // Access the class body through block() method
        Python3Parser.BlockContext blockContext = ctx.block();

        // Iterate over the statements inside the class body (block)
        for (Python3Parser.StmtContext stmt : blockContext.stmt()) {
            ASTNode node = visit(stmt);
            if (node instanceof MethodDeclaration) {
                typeDeclaration.addMethod((MethodDeclaration) node);
            }
        }

        return typeDeclaration;
    }

    @Override
    public ASTNode visitFuncdef(Python3Parser.FuncdefContext ctx) {
        // Create a MethodDeclaration for the function
        MethodDeclaration methodDeclaration = new MethodDeclaration();
        methodDeclaration.setName(ctx.name().getText()); // Function name

        // Visit parameters
        Python3Parser.ParametersContext parametersCtx = ctx.parameters();
        if (parametersCtx != null && parametersCtx.typedargslist() != null) {
            for (Python3Parser.TfpdefContext paramCtx : parametersCtx.typedargslist().tfpdef()) {
                Parameter parameter = new Parameter(paramCtx.name().getText());
                methodDeclaration.addParameter(parameter);
            }
        }

        // Visit the method body (using block() instead of suite())
        Python3Parser.BlockContext blockContext = ctx.block();
        ASTNode body = visit(blockContext);  // Visit the statements in the block
        methodDeclaration.setBody(body);

        return methodDeclaration;
    }

    @Override
    public ASTNode visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {
        // Placeholder for expression statements
        // For now, we'll skip detailed handling of expressions
        return null;
    }
}

/*public class PythonASTBuilder extends Python3ParserBaseVisitor<ASTNode> {

    private final static Logger logger = LoggerFactory.getLogger(PythonASTBuilder.class);

    @Override
    public ASTNode visitFile_input(Python3Parser.File_inputContext ctx) {
        logger.debug("Visiting File_input...");
        List<ASTNode> body = new ArrayList<>();
        for (Python3Parser.StmtContext stmt : ctx.stmt()) {
            ASTNode stmtNode = visit(stmt);
            if (stmtNode != null) {
                body.add(stmtNode);
            }
        }
        return ASTNodeFactory.createModuleNode(body);
    }

    @Override
    public ASTNode visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {
        // Check for function call or simple expression
        if (ctx.testlist() != null) {
            return visit(ctx.testlist());  // Handle function call expressions like print("x is greater than 10")
        }
        return ASTNodeFactory.createExpressionNode(ctx.getText());
    }

    @Override
    public ASTNode visitTest(Python3Parser.TestContext ctx) {
        return ASTNodeFactory.createExpressionNode(ctx.getText());
    }

    private void visitBlock(Python3Parser.BlockContext blockContext, List<ASTNode> body) {
        if (blockContext == null) {
            return;
        }

        for (Python3Parser.StmtContext stmt : blockContext.stmt()) {
            System.out.println(stmt.getText());
            ASTNode node = visit(stmt);
            if (node != null) {
                System.out.println("Node added: " + node);  // Debug output
                body.add(node);  // Add the node to the body list
            } else {
                // Log if the node is null, to help debug issues
                System.out.println("Null node returned for stmt: " + stmt.getText());
            }
        }
    }

    @Override
    public ASTNode visitStmt(Python3Parser.StmtContext ctx) {
        System.out.println("Visiting stmt: " + ctx.getText());

        if (ctx.simple_stmts() == null && ctx.compound_stmt() == null) {
            System.out.println("Unknown statement: " + ctx.getText());
        }

        // Handle simple statements like assignments or expressions
        if (ctx.simple_stmts() != null) {
            System.out.println("Visiting simple statement: " + ctx.simple_stmts().getText());
            return visit(ctx.simple_stmts());  // Visit the simple statement (e.g., assignment)
        }

        // Handle compound statements like if, while, for, etc.
        if (ctx.compound_stmt() != null) {
            System.out.println("Visiting compound statement: " + ctx.compound_stmt().getText());
            return visit(ctx.compound_stmt());  // Visit compound statements like if, while, etc.
        }

        // Return null if no matching rule is found
        return null;
    }


    @Override
    public ASTNode visitSimple_stmt(Python3Parser.Simple_stmtContext ctx) {
        // Handle simple statements, e.g., assignments or expressions
        return visit(ctx.expr_stmt());
    }

    @Override
    public ASTNode visitCompound_stmt(Python3Parser.Compound_stmtContext ctx) {
        if (ctx.if_stmt() != null) {
            return visitIf_stmt(ctx.if_stmt());
        } else if (ctx.for_stmt() != null) {
            return visitFor_stmt(ctx.for_stmt());
        } else if (ctx.while_stmt() != null) {
            return visitWhile_stmt(ctx.while_stmt());
        }
        // Add more compound statement types as needed
        return null;
    }

    @Override
    public ASTNode visitClassdef(Python3Parser.ClassdefContext ctx) {
        String name = ctx.name().getText();
        List<ASTNode> body = new ArrayList<>();

        for (Python3Parser.StmtContext stmtCtx : ctx.block().stmt()) {
            ASTNode stmtNode = visit(stmtCtx);
            if (stmtNode != null) {
                body.add(stmtNode);
            }
        }

        return ASTNodeFactory.createClassNode(name, body);
    }

    @Override
    public ASTNode visitFuncdef(Python3Parser.FuncdefContext ctx) {
        String name = ctx.name().getText();
        List<ASTNode> params = new ArrayList<>();
        List<ASTNode> body = new ArrayList<>();

        if (ctx.parameters() != null && ctx.parameters().typedargslist() != null) {
            for (Python3Parser.TfpdefContext paramCtx : ctx.parameters().typedargslist().tfpdef()) {
                params.add(ASTNodeFactory.createParamNode(paramCtx.getText()));
            }
        }

        for (Python3Parser.StmtContext stmtCtx : ctx.block().stmt()) {
            ASTNode stmtNode = visit(stmtCtx);
            if (stmtNode != null) {
                body.add(stmtNode);
            }
        }

        return ASTNodeFactory.createFunctionNode(name, params, body);
    }


    @Override
    public ASTNode visitIf_stmt(Python3Parser.If_stmtContext ctx) {
        // Extract the condition
        Python3Parser.TestContext conditionContext = ctx.test(0);
        ASTNode condition = visit(conditionContext);

        // Extract the body
        List<ASTNode> body = new ArrayList<>();
        visitBlock(ctx.block(0), body);

        // Extract elif clauses
        List<ElifNode> elifs = new ArrayList<>();
        for (int i = 0; i < ctx.ELIF().size(); i++) {
            ASTNode elifCondition = visit(ctx.test(i + 1));
            List<ASTNode> elifBody = new ArrayList<>();
            visitBlock(ctx.block(i + 1), elifBody);
            elifs.add(new ElifNode(elifCondition, elifBody));
        }

        // Extract the else clause
        List<ASTNode> elseBody = new ArrayList<>();
        if (ctx.ELSE() != null) {
            visitBlock(ctx.block(ctx.ELIF().size() + 1), elseBody);
        }
        // Create and return the custom IfNode
        return ASTNodeFactory.createIfNode(condition, body, elifs, elseBody);
    }

    @Override
    public ASTNode visitWhile_stmt(Python3Parser.While_stmtContext ctx) {
        ASTNode condition = visit(ctx.test());
        List<ASTNode> body = new ArrayList<>();

        for (Python3Parser.BlockContext blockCtx : ctx.block()) {
            for (Python3Parser.StmtContext stmtCtx : blockCtx.stmt()) {
                ASTNode stmtNode = visit(stmtCtx);
                if (stmtNode != null) {
                    body.add(stmtNode);
                }
            }
        }

        return ASTNodeFactory.createWhileNode(condition, body);
    }

    @Override
    public ASTNode visitFor_stmt(Python3Parser.For_stmtContext ctx) {
        List<ASTNode> iterables = new ArrayList<>();
        List<ASTNode> body = new ArrayList<>();

        if (ctx.exprlist() != null) {
            iterables.add(visit(ctx.exprlist()));
        }

        if (ctx.testlist() != null) {
            iterables.add(visit(ctx.testlist()));
        }

        for (Python3Parser.BlockContext blockCtx : ctx.block()) {
            for (Python3Parser.StmtContext stmtCtx : blockCtx.stmt()) {
                ASTNode stmtNode = visit(stmtCtx);
                if (stmtNode != null) {
                    body.add(stmtNode);
                }
            }
        }

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
            body.add(ASTNodeFactory.createElseNode(elseBody));
        }

        return ASTNodeFactory.createForNode(iterables, body);
    }

  /*  @Override
    public ASTNode visitComparison(Python3Parser.ComparisonContext ctx) {
        // Extract the left and right expressions and the operator
        String left = ctx.expr(0).getText();  // "x"
        String operator = ctx.comp_op().toString();  // "=="
        String right = ctx.expr(1).getText();  // "10"

        // Debugging: print out the comparison parts
        System.out.println("Left: " + left + ", Operator: " + operator + ", Right: " + right);

        // Create and return an AST node for the comparison
        return ASTNodeFactory.createComparisonNode(left, operator, right);
    }*/




 /*   @Override
    public ASTNode visitTest(Python3Parser.TestContext ctx) {
        System.out.println("Visiting test: " + ctx.getText());
        if (ctx.or_test().size() == 1) {
            return visit(ctx.or_test(0)); // Single or_test condition
        } else if (ctx.IF() != null && ctx.ELSE() != null) {
            // Handle Python ternary condition (e.g., x if y else z)
            ASTNode condition = visit(ctx.or_test(1));  // y in x if y else z
            ASTNode trueBranch = visit(ctx.or_test(0)); // x in x if y else z
            ASTNode falseBranch = visit(ctx.test());    // z in x if y else z
            return new TernaryExpressionNode(condition, trueBranch, falseBranch);
        }
        return null;  // Return null if test rule doesn't match known patterns
    }


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
    }*/

    // Additional visit methods for other Python constructs can be added as needed.
