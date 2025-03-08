package antlr.ast.builder.python;

import antlr.ast.node.ASTNode;
import antlr.ast.node.declaration.MethodDeclaration;
import antlr.ast.node.declaration.TypeDeclaration;
import antlr.ast.node.expression.Assignment;
import antlr.ast.node.expression.InfixExpression;
import antlr.ast.node.misc.Identifier;
import antlr.ast.node.misc.Parameter;
import antlr.ast.node.statement.Block;
import antlr.ast.node.statement.IfStatement;
import antlr.ast.node.statement.ForStatement;
import antlr.ast.node.statement.ReturnStatement;
import antlr.ast.node.statement.WhileStatement;
import antlr.ast.node.unit.CompilationUnit;
import antlr.base.python.Python3Parser;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class ASTNodeFactory {

    public static CompilationUnit createCompilationUnit(Python3Parser.File_inputContext ctx) {
        return new CompilationUnit(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
    }

    public static TypeDeclaration createTypeDeclaration(Python3Parser.ClassdefContext ctx) {
        TypeDeclaration type = new TypeDeclaration(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
        type.setName(ctx.name().getText());
        return type;
    }

    public static MethodDeclaration createMethodDeclaration(String name, ParserRuleContext ctx, List<Parameter> parameters, Block body) {
        MethodDeclaration method = new MethodDeclaration(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
        method.setName(name);
        if (parameters != null) {
            parameters.forEach(method::addParameter);
        }
        if (body != null) {
            method.setBody(body);
        }
        return method;
    }

    public static Parameter createParameter(String name, ParserRuleContext ctx) {
        return new Parameter(name, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
    }

    public static Identifier createIdentifier(String name, ParserRuleContext ctx) {
        return new Identifier(name, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
    }

    public static Block createBlock(ParserRuleContext ctx, List<ASTNode> statements) {
        Block block = new Block(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
        if (statements != null) {
            statements.forEach(block::addStatement);
        }
        return block;
    }

    public static ReturnStatement createReturnStatement(ASTNode expression, ParserRuleContext ctx) {
        ReturnStatement returnStatement = new ReturnStatement("ReturnStatement", ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(), ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
        if (expression != null) {
            returnStatement.setExpression(expression);
        }
        return returnStatement;
    }

    public static Assignment createAssignment(String operator, ASTNode left, ASTNode right, ParserRuleContext ctx) {
        return new Assignment(operator, left, right, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
    }

    public static InfixExpression createInfixExpression(ASTNode left, String operator, ASTNode right, ParserRuleContext ctx) {
        return new InfixExpression(left, operator, right, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
    }

    public static IfStatement createIfStatement(ASTNode condition, Block body, Block elseBody, ParserRuleContext ctx) {
        return new IfStatement(condition, body, elseBody, ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(), ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
    }

    public static ForStatement createForStatement(List<ASTNode> loopVariables, Block loopBody, Block elseBody, ParserRuleContext ctx) {
        return new ForStatement(loopVariables, loopBody, elseBody, ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(), ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
    }

    public static WhileStatement createWhileStatement(ASTNode condition, Block body, ParserRuleContext ctx) {
        return new WhileStatement("WhileStatement", ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine());
    }
}