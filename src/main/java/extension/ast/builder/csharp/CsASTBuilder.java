package extension.ast.builder.csharp;

import extension.ast.builder.LangASTBuilder;
import extension.ast.node.LangASTNode;
import extension.base.lang.csharp.CSharpParser;
import extension.base.lang.csharp.CSharpParserBaseVisitor;

/**
 * Builder class to traverse the ANTLR parse tree for C# and build the custom AST (base only).
 * This base delegates by default to the compilation unit entry and leaves sub-builders for future work.
 */
public class CsASTBuilder extends CSharpParserBaseVisitor<LangASTNode> implements LangASTBuilder<CSharpParser.Compilation_unitContext> {

    public CsASTBuilder() { }

    public LangASTNode build(CSharpParser.Compilation_unitContext ctx) { return visitCompilation_unit(ctx); }

}
