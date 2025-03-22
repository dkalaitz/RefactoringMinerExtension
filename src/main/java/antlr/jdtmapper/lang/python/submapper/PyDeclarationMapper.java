package antlr.jdtmapper.lang.python.submapper;

import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.jdtmapper.lang.python.PyJdtASTMapper;
import org.eclipse.jdt.core.dom.*;

/**
 * Maps Python declaration nodes to JDT declaration nodes
 */
public class PyDeclarationMapper {

    /**
     * Helper method to add a modifier to a type declaration.
     *
     * @param typeDecl The type declaration to add the modifier to
     * @param modifier The modifier to add
     */
    @SuppressWarnings("unchecked")
    private void addModifier(TypeDeclaration typeDecl, Modifier modifier) {
        typeDecl.modifiers().add(modifier);
    }

    /**
     * Helper method to add a modifier to a method declaration.
     *
     * @param methodDecl The method declaration to add the modifier to
     * @param modifier The modifier to add
     */
    @SuppressWarnings("unchecked")
    private void addModifierToMethod(MethodDeclaration methodDecl, Modifier modifier) {
        methodDecl.modifiers().add(modifier);
    }

    /**
     * Helper method to add a body declaration to a type declaration.
     *
     * @param typeDecl The type declaration to add the body declaration to
     * @param bodyDecl The body declaration to add
     */
    @SuppressWarnings("unchecked")
    private void addBodyDeclaration(TypeDeclaration typeDecl, BodyDeclaration bodyDecl) {
        typeDecl.bodyDeclarations().add(bodyDecl);
    }

    /**
     * Helper method to add a parameter to a method declaration.
     *
     * @param methodDecl The method declaration to add the parameter to
     * @param param The parameter to add
     */
    @SuppressWarnings("unchecked")
    private void addParameter(MethodDeclaration methodDecl, SingleVariableDeclaration param) {
        methodDecl.parameters().add(param);
    }

    /**
     * Maps a LangTypeDeclaration node to a JDT TypeDeclaration node.
     *
     * @param langTypeDeclaration The LangTypeDeclaration node to map
     * @param jdtAst The JDT AST to create nodes with
     * @param pyJdtASTMapper Reference to the main mapper for delegating child node mapping
     * @return A JDT TypeDeclaration node
     */
    public TypeDeclaration mapTypeDeclaration(LangTypeDeclaration langTypeDeclaration, AST jdtAst, PyJdtASTMapper pyJdtASTMapper) {
        if (langTypeDeclaration == null) return null;

        TypeDeclaration typeDecl = jdtAst.newTypeDeclaration();

        // Set class name
        typeDecl.setName(jdtAst.newSimpleName(langTypeDeclaration.getName()));

        // Python classes are public by default
        addModifier(typeDecl, jdtAst.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));

        // Add methods
        if (langTypeDeclaration.getMethods() != null) {
            for (LangMethodDeclaration methodDecl : langTypeDeclaration.getMethods()) {
                MethodDeclaration jdtMethod = mapMethodDeclaration(methodDecl, jdtAst, pyJdtASTMapper);
                if (jdtMethod != null) {
                    addBodyDeclaration(typeDecl, jdtMethod);
                }
            }
        }

        return typeDecl;
    }

    /**
     * Maps a LangMethodDeclaration node to a JDT MethodDeclaration node.
     *
     * @param langMethodDeclaration The LangMethodDeclaration node to map
     * @param jdtAst The JDT AST to create nodes with
     * @param pyJdtASTMapper Reference to the main mapper for delegating child node mapping
     * @return A JDT MethodDeclaration node
     */
    public MethodDeclaration mapMethodDeclaration(LangMethodDeclaration langMethodDeclaration, AST jdtAst, PyJdtASTMapper pyJdtASTMapper) {
        if (langMethodDeclaration == null) return null;

        MethodDeclaration methodDecl = jdtAst.newMethodDeclaration();

        // Set method name
        methodDecl.setName(jdtAst.newSimpleName(langMethodDeclaration.getName()));

        // Python methods are public by default
        addModifierToMethod(methodDecl, jdtAst.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));

        // Map parameters
        if (langMethodDeclaration.getParameters() != null) {
            for (LangSingleVariableDeclaration param : langMethodDeclaration.getParameters()) {
                SingleVariableDeclaration jdtParam = mapSingleVariableDeclaration(param, jdtAst);
                if (jdtParam != null) {
                    addParameter(methodDecl, jdtParam);
                }
            }
        }

        // Map body if available
        if (langMethodDeclaration.getBody() != null) {
            methodDecl.setBody(pyJdtASTMapper.mapBlock(langMethodDeclaration.getBody(), jdtAst));
        }

        // Set return type (in Python all methods return Object unless specified)
        methodDecl.setReturnType2(jdtAst.newSimpleType(jdtAst.newSimpleName("Object")));

        return methodDecl;
    }

    /**
     * Maps a LangSingleVariableDeclaration node to a JDT SingleVariableDeclaration node.
     *
     * @param langVar The LangSingleVariableDeclaration node to map
     * @param jdtAst The JDT AST to create nodes with
     * @return A JDT SingleVariableDeclaration node
     */
    public SingleVariableDeclaration mapSingleVariableDeclaration(LangSingleVariableDeclaration langVar, AST jdtAst) {
        if (langVar == null) return null;

        SingleVariableDeclaration varDecl = jdtAst.newSingleVariableDeclaration();

        // Set variable name
        varDecl.setName(jdtAst.newSimpleName(langVar.getSimpleName().getIdentifier()));

        // Python is dynamically typed, so use Object as the type
        varDecl.setType(jdtAst.newSimpleType(jdtAst.newSimpleName("Object")));

        return varDecl;
    }

}