package antlr.ast.node;

public enum NodeTypeEnum {
    /** COMPILATION UNIT */
    COMPILATION_UNIT("LangCompilationUnit"),

    /** DECLARATIONS */
    TYPE_DECLARATION("LangTypeDeclaration"),
    METHOD_DECLARATION("LangMethodDeclaration"),
    SINGLE_VARIABLE_DECLARATION("LangSingleVariableDeclaration"),

    /** EXPRESSIONS */
    SIMPLE_NAME("LangSimpleName"),
    INFIX_EXPRESSION("LangInfixExpression"),
    ASSIGNMENT("LangAssignment"),
    METHOD_INVOCATION("LangMethodInvocation"),
    FIELD_ACCESS("LangFieldAccess"),

    /** STATEMENTS */
    BLOCK("LangBlock"),
    IF_STATEMENT("LangIfStatement"),
    WHILE_STATEMENT("LangWhileStatement"),
    FOR_STATEMENT("LangForStatement"),
    RETURN_STATEMENT("LangReturnStatement"),
    EXPRESSION_STATEMENT("LangExpressionStatement"),
    DICTIONARY_LITERAL("LangDictionaryLiteral"),
    IMPORT_STATEMENT("LangImportStatement"),

    /** LITERALS */
    LIST_LITERAL("LangListLiteral"),
    TUPLE_LITERAL("LangTupleLiteral"),
    BOOLEAN_LITERAL("LangBooleanLiteral"),
    INTEGER_LITERAL("LangIntegerLiteral"),
    STRING_LITERAL("LangStringLiteral"),
    NULL_LITERAL("LangNullLiteral"),

    /** COMMENTS */
    COMMENT("LangComment");

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
