package antlr.python.ast.builder;

import antlr.python.ast.node.ASTNode;
import antlr.python.ast.node.child.*;

import java.util.List;

public class ASTNodeFactory {

    public static ModuleNode createModuleNode(List<ASTNode> body) {
        System.out.println("Creating ModuleNode with body size: " + body.size());
        return new ModuleNode(body);
    }

    public static FunctionDefNode createFunctionNode(String name, List<ASTNode> params, List<ASTNode> body) {
        System.out.println("Creating FunctionDefNode for function: " + name);
        return new FunctionDefNode(name, params, body);
    }

    public static ParamNode createParamNode(String name) {
        System.out.println("Creating ParamNode for parameter: " + name);
        return new ParamNode(name);
    }

    public static ExpressionNode createExpressionNode(String expression) {
        System.out.println("Creating ExpressionNode for expression: " + expression);
        return new ExpressionNode(expression);
    }

    public static ClassNode createClassNode(String name, List<ASTNode> body) {
        System.out.println("Creating ClassNode for class: " + name);
        return new ClassNode(name, body);
    }

    public static WhileNode createWhileNode(ASTNode condition, List<ASTNode> body) {
        System.out.println("Creating WhileNode with condition: " + condition);
        return new WhileNode(condition, body);
    }

    public static ForNode createForNode(List<ASTNode> iterables, List<ASTNode> body) {
        System.out.println("Creating ForNode with iterables: " + iterables.size());
        return new ForNode(iterables, body);
    }

    public static IfNode createIfNode(ASTNode condition, List<ASTNode> body, List<ElifNode> elifs, List<ASTNode> elseBody) {
        System.out.println("Creating IfNode with condition: " + condition);
        return new IfNode(condition, body, elifs, elseBody);
    }

    public static ElifNode createElifNode(ASTNode condition, List<ASTNode> body) {
        System.out.println("Creating ElifNode with condition: " + condition);
        return new ElifNode(condition, body);
    }

    public static ElseNode createElseNode(List<ASTNode> elseBody) {
        System.out.println("Creating ElseNode with body size: " + elseBody.size());
        return new ElseNode(elseBody);
    }


}
