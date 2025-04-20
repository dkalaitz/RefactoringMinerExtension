package antlr.jdtmapper.lang.python.submapper;

import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.ast.node.unit.LangCompilationUnit;
import antlr.jdtmapper.lang.python.PyJdtASTMapper;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.ArrayList;

import static antlr.jdtmapper.BaseJdtASTMapper.setSourceRange;

/**
 * Maps Python compilation unit nodes to JDT compilation unit nodes
 */
public class PyCompilationUnitMapper {

    /**
     * Helper method to add a type declaration to a compilation unit.
     *
     * @param cu The compilation unit to add the type to
     * @param typeDecl The type declaration to add
     */
    @SuppressWarnings("unchecked")
    private void addTypeToCompilationUnit(CompilationUnit cu, TypeDeclaration typeDecl) {
        cu.types().add(typeDecl);
    }

    /**
     * Maps a LangCompilationUnit node to a JDT CompilationUnit node.
     *
     * @param langCompilationUnit The LangCompilationUnit node to map
     * @param jdtAst              The JDT AST to create nodes with
     * @param pyJdtASTMapper      Reference to the main mapper for delegating child node mapping
     * @return A JDT CompilationUnit node
     */
    public CompilationUnit mapCompilationUnit(LangCompilationUnit langCompilationUnit, AST jdtAst, PyJdtASTMapper pyJdtASTMapper) {
        if (langCompilationUnit == null) return null;

        CompilationUnit cu = jdtAst.newCompilationUnit();

        setSourceRange(cu, langCompilationUnit);

        // Map type declarations (classes)
        if (langCompilationUnit.getTypes() != null) {
            for (LangTypeDeclaration typeDecl : langCompilationUnit.getTypes()) {
                TypeDeclaration jdtType = pyJdtASTMapper.mapTypeDeclaration(typeDecl, jdtAst);
                if (jdtType != null) {
                    addTypeToCompilationUnit(cu, jdtType);
                }
            }
        }

        return cu;
    }


}
