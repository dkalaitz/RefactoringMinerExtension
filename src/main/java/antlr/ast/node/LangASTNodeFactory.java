package antlr.ast.node;

import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.ast.node.expression.LangAssignment;
import antlr.ast.node.expression.LangInfixExpression;
import antlr.ast.node.expression.LangMethodInvocation;
import antlr.ast.node.literal.LangBooleanLiteral;
import antlr.ast.node.literal.LangIntegerLiteral;
import antlr.ast.node.literal.LangStringLiteral;
import antlr.ast.node.expression.LangSimpleName;
import antlr.ast.node.statement.*;
import antlr.ast.node.unit.LangCompilationUnit;
import antlr.base.lang.python.Python3Parser;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class LangASTNodeFactory {

    public static LangCompilationUnit createCompilationUnit(Python3Parser.File_inputContext ctx) {
        return new LangCompilationUnit(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
    }

    public static LangTypeDeclaration createTypeDeclaration(Python3Parser.ClassdefContext ctx) {
        LangTypeDeclaration type = new LangTypeDeclaration(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
        type.setName(ctx.name().getText());
        return type;
    }

    public static LangMethodDeclaration createMethodDeclaration(String name, ParserRuleContext ctx, List<LangSingleVariableDeclaration> langSingleVariableDeclarations, LangBlock body) {
        LangMethodDeclaration method = new LangMethodDeclaration(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
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
        return new LangSingleVariableDeclaration(langSimpleName, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
    }

    public static LangSimpleName createSimpleName(String name, ParserRuleContext ctx) {
        return new LangSimpleName(name, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
    }

    public static LangBlock createBlock(ParserRuleContext ctx, List<LangASTNode> statements) {
        LangBlock langBlock = new LangBlock(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
        if (statements != null) {
            statements.forEach(langBlock::addStatement);
        }
        return langBlock;
    }

    public static LangReturnStatement createReturnStatement(LangASTNode expression, ParserRuleContext ctx) {
        LangReturnStatement langReturnStatement = new LangReturnStatement("LangReturnStatement", ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(), ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
        if (expression != null) {
            langReturnStatement.setExpression(expression);
        }
        return langReturnStatement;
    }

    public static LangAssignment createAssignment(String operator, LangASTNode left, LangASTNode right, ParserRuleContext ctx) {
        return new LangAssignment(operator, left, right, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
    }

    public static LangInfixExpression createInfixExpression(LangASTNode left, LangASTNode right, String operator, ParserRuleContext ctx) {
        return new LangInfixExpression(left, operator, right, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
    }

    public static LangIfStatement createIfStatement(LangASTNode condition, LangBlock body, LangBlock elseBody, ParserRuleContext ctx) {
        return new LangIfStatement(condition, body, elseBody, ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(), ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
    }

    public static LangForStatement createForStatement(List<LangSingleVariableDeclaration> initializers, LangASTNode condition,
                                                      List<LangASTNode> updates, LangBlock loopBody,
                                                      LangBlock elseBody, ParserRuleContext ctx) {
        return new LangForStatement(initializers, condition, updates, loopBody, elseBody,
                ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
    }

    public static LangWhileStatement createWhileStatement(LangASTNode condition, LangBlock body, LangBlock elseBody, ParserRuleContext ctx) {
        return new LangWhileStatement("LangWhileStatement", ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine(), condition, body, elseBody);
    }

    public static LangMethodInvocation createMethodInvocation(ParserRuleContext ctx) {
        return new LangMethodInvocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
    }

    public static LangIntegerLiteral createIntegerLiteral(ParserRuleContext ctx, String value) {
        try {
            int intValue = Integer.parseInt(value);
            return new LangIntegerLiteral(
                    ctx.getStart().getLine(),
                    ctx.getStart().getCharPositionInLine(),
                    ctx.getStop().getLine(),
                    ctx.getStop().getCharPositionInLine(),
                    intValue
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer value: " + value);
        }
    }

    // Factory method for creating LangStringLiteral nodes
    public static LangStringLiteral createStringLiteral(ParserRuleContext ctx, String value) {
        // Remove the surrounding quotes from the string value
        String unquotedValue = value.length() >= 2 ? value.substring(1, value.length() - 1) : value;
        return new LangStringLiteral(
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(),
                ctx.getStop().getCharPositionInLine(),
                unquotedValue
        );
    }

    // Factory method for creating LangBooleanLiteral nodes
    public static LangBooleanLiteral createBooleanLiteral(ParserRuleContext ctx, boolean value) {
        return new LangBooleanLiteral(
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(),
                ctx.getStop().getCharPositionInLine(),
                value
        );
    }

    public static LangExpressionStatement createExpressionStatement(LangASTNode expression, ParserRuleContext ctx) {
        LangExpressionStatement statement = new LangExpressionStatement(
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(),
                ctx.getStop().getCharPositionInLine()
        );

        if (expression != null) {
            statement.setExpression(expression);
        }

        return statement;
    }


}