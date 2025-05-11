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
    PREFIX_EXPRESSION("LangPrefixExpression"),
    POSTFIX_EXPRESSION("LangPostfixExpression"),

    /** STATEMENTS */
    BLOCK("LangBlock"),
    IF_STATEMENT("LangIfStatement"),
    WHILE_STATEMENT("LangWhileStatement"),
    FOR_STATEMENT("LangForStatement"),
    RETURN_STATEMENT("LangReturnStatement"),
    EXPRESSION_STATEMENT("LangExpressionStatement"),
    DICTIONARY_LITERAL("LangDictionaryLiteral"),
    IMPORT_STATEMENT("LangImportStatement"),
    TRY_STATEMENT("LangTryStatement"),
    CATCH_CLAUSE("LangCatchClause"),
    THROW_STATEMENT("LangThrowStatement"), // raise for python
    BREAK_STATEMENT("LangBreakStatement"),
    CONTINUE_STATEMENT("LangContinueStatement"),
    SWITCH_STATEMENT("LangSwitchStatement"), // match for python
    CASE_STATEMENT("LangCaseStatement"),

    /** LITERALS */
    LIST_LITERAL("LangListLiteral"),
    TUPLE_LITERAL("LangTupleLiteral"),
    BOOLEAN_LITERAL("LangBooleanLiteral"),
    INTEGER_LITERAL("LangIntegerLiteral"),
    STRING_LITERAL("LangStringLiteral"),
    NULL_LITERAL("LangNullLiteral"),

    /** METADATA */
    ANNOTATION("LangAnnotation"),
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
