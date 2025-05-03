package antlr.ast.node;

import antlr.ast.builder.python.PyASTBuilderUtil;
import antlr.ast.node.comment.LangComment;
import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.ast.node.expression.*;
import antlr.ast.node.literal.*;
import antlr.ast.node.statement.*;
import antlr.ast.node.unit.LangCompilationUnit;
import antlr.base.lang.python.Python3Parser;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class LangASTNodeFactory {

    /** Compilation Unit */
    public static LangCompilationUnit createCompilationUnit(Python3Parser.File_inputContext ctx) {
        return new LangCompilationUnit(PositionUtils.getPositionInfo(ctx));
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
        return new LangAssignment(operator, left, right, PositionUtils.getPositionInfo(ctx));
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

    /** Statements */
    public static LangBlock createBlock(ParserRuleContext ctx, List<LangASTNode> statements) {
        LangBlock langBlock = new LangBlock(PositionUtils.getPositionInfo(ctx));
        if (statements != null) {
            statements.forEach(langBlock::addStatement);
        }
        return langBlock;
    }

    public static LangIfStatement createIfStatement(LangASTNode condition, LangBlock body, LangBlock elseBody, ParserRuleContext ctx) {
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
        }

        return statement;
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

    /** Comments */
    public static LangComment createComment(ParserRuleContext ctx, String commentContent, boolean isBlockComment, boolean isDocComment) {
        return new LangComment(commentContent, isBlockComment, isDocComment, PositionUtils.getPositionInfo(ctx));
    }


}