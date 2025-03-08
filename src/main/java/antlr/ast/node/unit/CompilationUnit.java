package antlr.ast.node.unit;

import antlr.ast.node.declaration.TypeDeclaration;
import antlr.ast.visitor.ASTVisitor;
import antlr.ast.node.ASTNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class representing the entire source file (CompilationUnit)
public class CompilationUnit extends ASTNode {
    private final List<TypeDeclaration> types = new ArrayList<>();

    public CompilationUnit(int startLine, int startChar, int endLine, int endChar) {
        super("CompilationUnit", startLine, startChar, endLine, endChar);
    }

    public List<TypeDeclaration> getTypes() {
        return Collections.unmodifiableList(types);
    }

    public void addType(TypeDeclaration type) {
        types.add(type);
        addChild(type);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        for (TypeDeclaration type : types) {
            type.accept(visitor);
        }
    }

    @Override
    public String toString() {
        return "CompilationUnit{" +
                "types=" + types +
                '}';
    }
}