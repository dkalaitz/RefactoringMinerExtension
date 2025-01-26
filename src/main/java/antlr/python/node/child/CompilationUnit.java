package antlr.python.node.child;

import antlr.python.ast.ASTVisitor;
import antlr.python.node.ASTNode;

import java.util.ArrayList;
import java.util.List;

// Class representing the entire source file (CompilationUnit)
public class CompilationUnit extends ASTNode {
    private List<TypeDeclaration> types = new ArrayList<>();

    public List<TypeDeclaration> getTypes() {
        return types;
    }

    public void addType(TypeDeclaration type) {
        types.add(type);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        for (TypeDeclaration type : types) {
            type.accept(visitor);
        }
    }
}