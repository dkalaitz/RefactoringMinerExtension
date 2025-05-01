package antlr.jdtmapper.lang.python.submapper;

import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.jdtmapper.lang.python.PyJdtASTMapper;
import org.eclipse.jdt.core.dom.*;

import static antlr.jdtmapper.BaseJdtASTMapper.setSourceRange;

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

        // Set source range
        setSourceRange(typeDecl, langTypeDeclaration);

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

        Modifier publicModifier = jdtAst.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
        publicModifier.setSourceRange(langTypeDeclaration.getStartOffset(), 6); // "public" is 6 chars

        // Add the modifier to the class declaration
        addModifier(typeDecl, publicModifier);


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
        System.out.println("Mapping method: " + langMethodDeclaration.getName());
        System.out.println("Lang method source position: start=" + langMethodDeclaration.getStartOffset() + ", length=" + langMethodDeclaration.getLength());

        MethodDeclaration methodDecl = jdtAst.newMethodDeclaration();

        setSourceRange(methodDecl, langMethodDeclaration);
       // pyJdtASTMapper.setSourceRange(methodDecl, langMethodDeclaration);

        // Set method name
        methodDecl.setName(jdtAst.newSimpleName(langMethodDeclaration.getName()));

        setSignatureOffsets(methodDecl, langMethodDeclaration);
//        methodDecl.setProperty("signatureStartOffset", Math.max(0, langMethodDeclaration.getStartChar()));
//        methodDecl.setProperty("signatureEndOffset", Math.max(langMethodDeclaration.getStartChar() + 1,
//                Math.min(langMethodDeclaration.getEndChar(), 76)));

        // Build a normalized Python signature string
        // Create a standardized Python signature property
//        StringBuilder pythonSignature = new StringBuilder("def ");
//        pythonSignature.append(langMethodDeclaration.getName()).append("(");
//
//        if (langMethodDeclaration.getParameters() != null) {
//            boolean first = true;
//            for (LangSingleVariableDeclaration param : langMethodDeclaration.getParameters()) {
//                if (!first) pythonSignature.append(", ");
//                pythonSignature.append(param.getSimpleName().getIdentifier());
//                first = false;
//            }
//        }
//
//        pythonSignature.append("):");
//
//        // Store signature as property on the method node
//        methodDecl.setProperty("pythonMethodSignature", pythonSignature.toString());
//

        // Python methods are public by default
       // addModifierToMethod(methodDecl, jdtAst.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));

        // Map parameters
        if (langMethodDeclaration.getParameters() != null) {
            for (LangSingleVariableDeclaration param : langMethodDeclaration.getParameters()) {
                SingleVariableDeclaration jdtParam = mapSingleVariableDeclaration(param, jdtAst, pyJdtASTMapper);
                if (jdtParam != null) {
                    addParameter(methodDecl, jdtParam);
                }
            }
        } else {
            System.out.println("No parameters found");
        }

        Modifier publicModifier = jdtAst.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);

// Set the source range on the modifier to match the start of the method
        publicModifier.setSourceRange(langMethodDeclaration.getStartOffset(), 6); // "public" is 6 chars

// Add the modifier to the method declaration
        methodDecl.modifiers().add(publicModifier);

        // Map body if available
        if (langMethodDeclaration.getBody() != null) {
            methodDecl.setBody(pyJdtASTMapper.mapBlock(langMethodDeclaration.getBody(), jdtAst));
            System.out.println("Body mapped successfully");
        } else {
            System.out.println("No body found");
        }


        // Set return type (in Python all methods return Object unless specified)
        methodDecl.setReturnType2(jdtAst.newSimpleType(jdtAst.newSimpleName("Object")));

        System.out.println("Lang position: start=" + langMethodDeclaration.getStartOffset() + ", length=" + langMethodDeclaration.getLength());
        System.out.println("Lang end char" + langMethodDeclaration.getEndOffset());

        System.out.println("After setSourceRange - Method position: " + methodDecl.getStartPosition() + ", length: " + methodDecl.getLength());


        // Print additional information about the method declaration
        System.out.println("Method details:");
        System.out.println("  - Name: " + methodDecl.getName());
        System.out.println("  - Return type: " + (methodDecl.getReturnType2() != null ? methodDecl.getReturnType2().toString() : "null"));
        System.out.println("  - Modifiers: " + methodDecl.modifiers());
        System.out.println("  - Has body: " + (methodDecl.getBody() != null));
        System.out.println("  - Parameters count: " + methodDecl.parameters().size());

        return methodDecl;
    }

    /**
     * Maps a LangSingleVariableDeclaration node to a JDT SingleVariableDeclaration node.
     *
     * @param langVar The LangSingleVariableDeclaration node to map
     * @param jdtAst The JDT AST to create nodes with
     * @return A JDT SingleVariableDeclaration node
     */
    public SingleVariableDeclaration mapSingleVariableDeclaration(LangSingleVariableDeclaration langVar, AST jdtAst, PyJdtASTMapper pyJdtASTMapper) {
        if (langVar == null) return null;

        SingleVariableDeclaration varDecl = jdtAst.newSingleVariableDeclaration();
        setSourceRange(varDecl, langVar);

        // Set variable name
        varDecl.setName(jdtAst.newSimpleName(langVar.getLangSimpleName().getIdentifier()));

        // Python is dynamically typed, so use Object as the type
        varDecl.setType(jdtAst.newSimpleType(jdtAst.newSimpleName("Object")));


        return varDecl;
    }

    /**
     * Sets signature-specific offset information in PropertyDescriptor format
     * for the UMLModelASTReader to use when extracting the actual signature.
     *
     * @param methodDecl The JDT MethodDeclaration to set properties on
     * @param langMethodDecl The source LangMethodDeclaration with position info
     */
    public void setSignatureOffsets(MethodDeclaration methodDecl, LangMethodDeclaration langMethodDecl) {
        // Create standardized Python signature correctly
        StringBuilder pythonSignature = new StringBuilder("def ");
        pythonSignature.append(langMethodDecl.getName()).append("(");

        if (langMethodDecl.getParameters() != null) {
            boolean firstParam = true;
            for (LangSingleVariableDeclaration param : langMethodDecl.getParameters()) {
                if (!firstParam) pythonSignature.append(", ");
                pythonSignature.append(param.getLangSimpleName().getIdentifier());
                firstParam = false;
            }
        }

        pythonSignature.append("):");

        // Store as property for UMLModelASTReader
        methodDecl.setProperty("pythonMethodSignature", pythonSignature.toString());

        // Set source range so the signature can be properly extracted
        int startPos = langMethodDecl.getStartOffset();
        int endPos = langMethodDecl.getEndOffset();
        methodDecl.setProperty("signatureStartPosition", startPos);
        methodDecl.setProperty("signatureEndPosition", endPos);
    }



}