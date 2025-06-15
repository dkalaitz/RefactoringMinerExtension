package antlr.ast.stringifier;

import antlr.ast.visitor.LangASTVisitor;

public interface LangASTFlattener extends LangASTVisitor {
    String getResult();
}
