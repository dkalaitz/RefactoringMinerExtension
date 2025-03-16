package antlr.ast.node.unit;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.ast.visitor.LangASTVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class representing the entire source file (LangCompilationUnit)
public class LangCompilationUnit extends LangASTNode {
    private final List<LangTypeDeclaration> types = new ArrayList<>();

    public LangCompilationUnit(int startLine, int startChar, int endLine, int endChar) {
        super("LangCompilationUnit", startLine, startChar, endLine, endChar);
    }

    public List<LangTypeDeclaration> getTypes() {
        return Collections.unmodifiableList(types);
    }

    public void addType(LangTypeDeclaration type) {
        types.add(type);
        addChild(type);
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
        for (LangTypeDeclaration type : types) {
            type.accept(visitor);
        }
    }

    @Override
    public String toString() {
        return "LangCompilationUnit{" +
                "types=" + types +
                '}';
    }
}