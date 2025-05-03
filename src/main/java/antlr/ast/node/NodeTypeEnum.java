package antlr.ast.node;

public enum NodeTypeEnum {
    COMPILATION_UNIT("LangCompilationUnit"),
    TYPE_DECLARATION("LangTypeDeclaration"),
    SINGLE_VARIABLE_DECLARATION("LangSingleVariableDeclaration"),
    METHOD_DECLARATION("LangMethodDeclaration"),
    SIMPLE_NAME("LangSimpleName"),
    INFIX_EXPRESSION("LangInfixExpression"),
    ASSIGNMENT("LangAssignment"),
    METHOD_INVOCATION("LangMethodInvocation"),
    BLOCK("LangBlock"),
    IF_STATEMENT("LangIfStatement"),
    WHILE_STATEMENT("LangWhileStatement"),
    FOR_STATEMENT("LangForStatement"),
    RETURN_STATEMENT("LangReturnStatement"),
    EXPRESSION_STATEMENT("LangExpressionStatement"),
    BOOLEAN_LITERAL("LangBooleanLiteral"),
    INTEGER_LITERAL("LangIntegerLiteral"),
    STRING_LITERAL("LangStringLiteral"),
    LIST_LITERAL("LangListLiteral");

    private final String name;

    NodeTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
