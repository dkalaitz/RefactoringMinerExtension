package antlr.jdtmapper;

import antlr.ast.node.LangASTNode;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;

public interface JdtASTMapper {
    ASTNode map(LangASTNode langASTNode, AST jdtAst);
}
