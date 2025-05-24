package antlr.ast.node;

import antlr.ast.builder.python.PyASTBuilderUtil;
import antlr.ast.node.metadata.LangAnnotation;
import antlr.ast.node.metadata.comment.LangComment;
import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.ast.node.expression.*;
import antlr.ast.node.literal.*;
import antlr.ast.node.pattern.LangLiteralPattern;
import antlr.ast.node.pattern.LangPattern;
import antlr.ast.node.pattern.LangVariablePattern;
import antlr.ast.node.statement.*;
import antlr.ast.node.unit.LangCompilationUnit;
import antlr.base.lang.python.Python3Parser;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;

public class LangASTNodeFactory {

    /** Compilation Unit */
    public static LangCompilationUnit createCompilationUnit(Python3Parser.File_inputContext ctx) {
        return new LangCompilationUnit(PositionUtils.getPositionInfo(ctx));
    }

    /**
     * Creates a basic import statement with position information only
     */
    public static LangImportStatement createImportStatement(ParserRuleContext ctx) {
        return new LangImportStatement(PositionUtils.getPositionInfo(ctx));
    }

    /**
     * Creates a standard import statement (import module [as alias])
     */
    public static LangImportStatement createImportStatement(String moduleName, String alias, PositionInfo positionInfo) {
        return new LangImportStatement(moduleName, alias, positionInfo);
    }

    /** Declarations */
    public static LangTypeDeclaration createTypeDeclaration(Python3Parser.ClassdefContext ctx) {
        LangTypeDeclaration type = new LangTypeDeclaration(PositionUtils.getPositionInfo(ctx));
        type.setName(ctx.name().getText());
        return type;
    }

    public static LangMethodDeclaration createMethodDeclaration(String name, ParserRuleContext ctx, List<LangSingleVariableDeclaration> langSingleVariableDeclarations, LangBlock body) {
        LangMethodDeclaration method = new LangMethodDeclaration(PositionUtils.getPositionInfo(ctx));
        method.setName(name);
        if (langSingleVariableDeclarations != null) {
            langSingleVariableDeclarations.forEach(method::addParameter);
        }
        if (body != null) {
            method.setBody(body);
        }
        return method;
    }

    public static LangSingleVariableDeclaration createSingleVariableDeclaration(String name, ParserRuleContext ctx) {
        LangSimpleName langSimpleName = createSimpleName(name, ctx);
        return new LangSingleVariableDeclaration(langSimpleName, PositionUtils.getPositionInfo(ctx));
    }

    /** Expressions */
    public static LangSimpleName createSimpleName(String name, ParserRuleContext ctx) {
        return new LangSimpleName(name, PositionUtils.getPositionInfo(ctx));
    }

    public static LangAssignment createAssignment(String operator, LangASTNode left, LangASTNode right, ParserRuleContext ctx) {
        LangAssignment langAssignment = new LangAssignment(operator, left, right, PositionUtils.getPositionInfo(ctx));
        langAssignment.addChild(left);
        langAssignment.addChild(right);
        return langAssignment;
    }

    public static LangInfixExpression createInfixExpression(LangASTNode left, LangASTNode right, String operatorSymbol, ParserRuleContext ctx) {
        OperatorEnum operator = OperatorEnum.fromSymbol(operatorSymbol);
        return new LangInfixExpression(left, operator, right, PositionUtils.getPositionInfo(ctx));
    }

    public static LangMethodInvocation createMethodInvocation(ParserRuleContext ctx) {
        return new LangMethodInvocation(PositionUtils.getPositionInfo(ctx));
    }

    public static LangFieldAccess createFieldAccess(LangASTNode expression, String fieldName, ParserRuleContext ctx) {
        LangSimpleName name = createSimpleName(fieldName, ctx);
        return new LangFieldAccess(expression, name, PositionUtils.getPositionInfo(ctx));
    }

    /** Prefix and Postfix Expressions */
    public static LangPrefixExpression createPrefixExpression(LangASTNode operand, String operatorSymbol, ParserRuleContext ctx) {
        LangPrefixExpression expression = new LangPrefixExpression(PositionUtils.getPositionInfo(ctx));
        expression.setOperand(operand);
        expression.setOperator(OperatorEnum.fromSymbol(operatorSymbol));
        return expression;
    }

    public static LangPostfixExpression createPostfixExpression(LangASTNode operand, String operatorSymbol, ParserRuleContext ctx) {
        LangPostfixExpression expression = new LangPostfixExpression(PositionUtils.getPositionInfo(ctx));
        expression.setOperand(operand);
        expression.setOperator(OperatorEnum.fromSymbol(operatorSymbol));
        return expression;
    }


    /** Statements */
    public static LangBlock createBlock(ParserRuleContext ctx, List<LangASTNode> statements) {
        LangBlock langBlock = new LangBlock(PositionUtils.getPositionInfo(ctx));
        if (statements != null) {
            statements.forEach(langBlock::addStatement);
        }
        return langBlock;
    }

    public static LangIfStatement createIfStatement(LangASTNode condition, LangBlock body, LangASTNode elseBody, ParserRuleContext ctx) {
        return new LangIfStatement(condition, body, elseBody, PositionUtils.getPositionInfo(ctx));
    }

    public static LangForStatement createForStatement(List<LangSingleVariableDeclaration> initializers, LangASTNode condition,
                                                      List<LangASTNode> updates, LangBlock loopBody,
                                                      LangBlock elseBody, ParserRuleContext ctx) {
        return new LangForStatement(initializers, condition, updates, loopBody, elseBody, PositionUtils.getPositionInfo(ctx));
    }

    public static LangWhileStatement createWhileStatement(LangASTNode condition, LangBlock body, LangBlock elseBody, ParserRuleContext ctx) {
        return new LangWhileStatement(condition, body, elseBody, PositionUtils.getPositionInfo(ctx));
    }

    public static LangReturnStatement createReturnStatement(LangASTNode expression, ParserRuleContext ctx) {
        LangReturnStatement langReturnStatement = new LangReturnStatement(PositionUtils.getPositionInfo(ctx));
        if (expression != null) {
            langReturnStatement.setExpression(expression);
        }
        return langReturnStatement;
    }

    public static LangExpressionStatement createExpressionStatement(LangASTNode expression, ParserRuleContext ctx) {
        LangExpressionStatement statement = new LangExpressionStatement(PositionUtils.getPositionInfo(ctx));
        if (expression != null) {
            statement.setExpression(expression);
            statement.addChild(expression);
        }

        return statement;
    }

    public static LangAwaitExpression createAwaitExpression(ParserRuleContext ctx, LangASTNode expression) {
        LangAwaitExpression awaitExpression = new LangAwaitExpression(PositionUtils.getPositionInfo(ctx));
        if (expression != null) {
            awaitExpression.setExpression(expression);
        }
        return awaitExpression;
    }

    public static LangLambdaExpression createLambdaExpression(ParserRuleContext ctx, LangASTNode body, List<LangASTNode> parameters) {
        return new LangLambdaExpression(PositionUtils.getPositionInfo(ctx), parameters, body);
    }

    /** Try-Catch-Finally */
    public static LangASTNode createTryStatement(PositionInfo positionInfo, LangBlock tryBlock, List<LangCatchClause> catchClauses, LangBlock elseBlock, LangBlock finallyBlock) {
        return new LangTryStatement(positionInfo, tryBlock, catchClauses, elseBlock, finallyBlock);
    }

    public static LangTryStatement createTryStatement(LangASTNode body, List<LangCatchClause> catchClauses,
                                                      LangASTNode finallyBlock, ParserRuleContext ctx) {
        LangTryStatement tryStatement = new LangTryStatement(PositionUtils.getPositionInfo(ctx));

        if (body != null) {
            tryStatement.setBody(body);
        }

        if (catchClauses != null) {
            for (LangCatchClause catchClause : catchClauses) {
                tryStatement.addCatchClause(catchClause);
            }
        }

        if (finallyBlock != null) {
            tryStatement.setFinally(finallyBlock);
        }

        return tryStatement;
    }

    public static LangCatchClause createCatchClause(ParserRuleContext ctx) {
        return new LangCatchClause(PositionUtils.getPositionInfo(ctx));
    }

    public static LangCatchClause createCatchClause(ParserRuleContext ctx,
                                                    List<LangASTNode> exceptionTypes,
                                                    LangSimpleName exceptionVariable,
                                                    LangASTNode body) {
        LangCatchClause catchClause = new LangCatchClause(PositionUtils.getPositionInfo(ctx));

        if (exceptionTypes != null) {
            for (LangASTNode exceptionType : exceptionTypes) {
                catchClause.addExceptionType(exceptionType);
            }
        }

        if (exceptionVariable != null) {
            catchClause.setExceptionVariable(exceptionVariable);
        }

        if (body != null) {
            catchClause.setBody(body);
        }

        return catchClause;
    }

    public static LangBreakStatement createBreakStatement(ParserRuleContext ctx) {
        return new LangBreakStatement(PositionUtils.getPositionInfo(ctx));
    }

    public static LangContinueStatement createContinueStatement(ParserRuleContext ctx) {
        return new LangContinueStatement(PositionUtils.getPositionInfo(ctx));
    }

    public static LangGlobalStatement createGlobalStatement(ParserRuleContext ctx, List<String> globalNames) {
        List<LangSimpleName> globalNameList = new ArrayList<>();
        for (String globalName : globalNames) {
            LangSimpleName name = createSimpleName(globalName, ctx);
            globalNameList.add(name);
        }
        return new LangGlobalStatement(PositionUtils.getPositionInfo(ctx), globalNameList);
    }

    public static LangPassStatement createPassStatement(ParserRuleContext ctx) {
        return new LangPassStatement(PositionUtils.getPositionInfo(ctx));
    }

    public static LangDelStatement createDelStatement(ParserRuleContext ctx, List<LangASTNode> targets) {
        return new LangDelStatement(PositionUtils.getPositionInfo(ctx), targets);
    }

    public static LangYieldStatement createYieldStatement(ParserRuleContext ctx, LangASTNode expression) {
        return new LangYieldStatement(PositionUtils.getPositionInfo(ctx), expression);
    }

    public static LangAssertStatement createAssertStatement(ParserRuleContext ctx, LangASTNode expression, LangASTNode message) {
        return new LangAssertStatement(PositionUtils.getPositionInfo(ctx), expression, message);
    }

    public static LangThrowStatement createThrowStatement(ParserRuleContext ctx, LangASTNode exception, LangASTNode fromExpr) {
        return new LangThrowStatement(PositionUtils.getPositionInfo(ctx), exception, fromExpr);
    }

    public static LangASTNode createWithStatement(Python3Parser.With_stmtContext ctx, List<LangASTNode> contextItems, LangBlock body) {
        return new LangWithStatement(PositionUtils.getPositionInfo(ctx), contextItems, body);
    }

    public static LangWithContextItem createWithContextItem(Python3Parser.With_itemContext ctx, LangASTNode expr, LangSimpleName alias) {
        return new LangWithContextItem(PositionUtils.getPositionInfo(ctx), expr, alias);
    }

    public static LangNonLocalStatement createNonlocalStatement(ParserRuleContext ctx, List<String> nonlocalNames) {
        List<LangSimpleName> nonlocalNameList = new ArrayList<>();
        for (String nonlocalName : nonlocalNames) {
            nonlocalNameList.add(createSimpleName(nonlocalName, ctx));
        }
        return new LangNonLocalStatement(PositionUtils.getPositionInfo(ctx), nonlocalNameList);
    }

    public static LangAsyncStatement createAsyncStatement(ParserRuleContext ctx, LangASTNode body) {
        return new LangAsyncStatement(PositionUtils.getPositionInfo(ctx), body);
    }

    public static LangSwitchStatement createSwitchStatement(ParserRuleContext ctx, LangASTNode expression, List<LangCaseStatement> body) {
        return new LangSwitchStatement(PositionUtils.getPositionInfo(ctx), expression, body);
    }

    public static LangCaseStatement createCaseStatement(ParserRuleContext ctx, LangASTNode expression, LangBlock body) {
        return new LangCaseStatement(PositionUtils.getPositionInfo(ctx), expression, body);
    }

    /** Literals */
    public static LangIntegerLiteral createIntegerLiteral(ParserRuleContext ctx, String value) {
        try {
            int intValue = Integer.parseInt(value);
            return new LangIntegerLiteral(PositionUtils.getPositionInfo(ctx), intValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer value: " + value);
        }
    }

    public static LangStringLiteral createStringLiteral(ParserRuleContext ctx, String value) {
        return new LangStringLiteral(PositionUtils.getPositionInfo(ctx), PyASTBuilderUtil.removeQuotes(value));
    }

    public static LangBooleanLiteral createBooleanLiteral(ParserRuleContext ctx, boolean value) {
        return new LangBooleanLiteral(PositionUtils.getPositionInfo(ctx), value);
    }

    public static LangListLiteral createListLiteral(ParserRuleContext ctx, List<LangASTNode> elements) {
        return new LangListLiteral(PositionUtils.getPositionInfo(ctx), elements);
    }

    public static LangTupleLiteral createTupleLiteral(ParserRuleContext ctx, List<LangASTNode> elements) {
        return new LangTupleLiteral(PositionUtils.getPositionInfo(ctx), elements);
    }

    public static LangDictionaryLiteral createDictionaryLiteral(ParserRuleContext ctx) {
        return new LangDictionaryLiteral(PositionUtils.getPositionInfo(ctx));
    }

    public static LangNullLiteral createNullLiteral(ParserRuleContext ctx) {
        return new LangNullLiteral(PositionUtils.getPositionInfo(ctx));
    }

    /** Metadata */
    public static LangAnnotation createAnnotation(ParserRuleContext ctx, LangSimpleName name) {
        return new LangAnnotation(name, PositionUtils.getPositionInfo(ctx));
    }

    public static LangAnnotation createAnnotation(ParserRuleContext ctx, LangSimpleName name, List<LangASTNode> arguments) {
        LangAnnotation annotation = new LangAnnotation(name, PositionUtils.getPositionInfo(ctx));
        if (arguments != null) {
            annotation.setArguments(arguments);
        }
        return annotation;
    }

    public static LangComment createComment(ParserRuleContext ctx, String commentContent, boolean isBlockComment, boolean isDocComment) {
        return new LangComment(commentContent, isBlockComment, isDocComment, PositionUtils.getPositionInfo(ctx));
    }

    /** PATTERN */
    public static LangLiteralPattern createLiteralPattern(ParserRuleContext ctx, Object value) {
        return new LangLiteralPattern(PositionUtils.getPositionInfo(ctx), value);
    }

    public static LangVariablePattern createVariablePattern(ParserRuleContext ctx, String name) {
        return new LangVariablePattern(PositionUtils.getPositionInfo(ctx), name);
    }


}