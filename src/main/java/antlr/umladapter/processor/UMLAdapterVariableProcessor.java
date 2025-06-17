package antlr.umladapter.processor;

import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.expression.LangAssignment;
import gr.uom.java.xmi.LocationInfo;
import gr.uom.java.xmi.UMLParameter;
import gr.uom.java.xmi.UMLType;
import gr.uom.java.xmi.VariableDeclarationContainer;
import gr.uom.java.xmi.decomposition.VariableDeclaration;

public class UMLAdapterVariableProcessor {

    public static void processVariableDeclarations(LangSingleVariableDeclaration param, UMLParameter umlParam, UMLType typeObject, String sourceFolder, String filePath, LangMethodDeclaration methodDecl){

        String name = param.getLangSimpleName().getIdentifier();
        LocationInfo location = new LocationInfo(sourceFolder, filePath, methodDecl, LocationInfo.CodeElementType.SINGLE_VARIABLE_DECLARATION);

//        // Extract annotations (Python decorators etc.)
//        List<UMLAnnotation> annotations = extractAnnotations(param, sourceFolder, filePath);
//
//        // Extract modifiers
//        List<UMLModifier> modifiers = extractModifiers(param, sourceFolder, filePath);

//        VariableDeclaration vd = new VariableDeclaration(
//                param.getRootCompilationUnit(),
//                name,
//                typeObject,
//                param.isVarArgs(),
//                location,
////                annotations,
////                modifiers
//        );

        VariableDeclaration vd = new VariableDeclaration(
                param.getRootCompilationUnit(),
                sourceFolder,
                filePath,
                param
        );

        vd.setAttribute(param.isAttribute());
        vd.setParameter(param.isParameter());

        umlParam.setVariableDeclaration(vd);
    }

    /**
     * Process assignment-based attribute declarations (self.attr = value)
     */
    public static VariableDeclaration processAttributeAssignment(LangAssignment assignment, String sourceFolder,
                                                                 String filePath, String attributeName,
                                                                 VariableDeclarationContainer container) {

        VariableDeclaration variableDeclaration = new VariableDeclaration(
                assignment.getRootCompilationUnit(),
                sourceFolder,
                filePath,
                assignment,
                container,
                attributeName
        );

        // Mark as attribute (this should already be set by the constructor)
        variableDeclaration.setAttribute(true);

        return variableDeclaration;
    }

//    private static List<UMLAnnotation> extractAnnotations(LangSingleVariableDeclaration param, String sourceFolder, String filePath) {
//        List<UMLAnnotation> umlAnnotations = new ArrayList<>();
//
//        List<LangAnnotation> langAnnotations = param.getAnnotations();
//
//        for (LangAnnotation langAnnotation : langAnnotations) {
//            UMLAnnotation umlAnnotation = new UMLAnnotation(
//                    langAnnotation.getRootCompilationUnit(),
//                    sourceFolder,
//                    filePath,
//                    langAnnotation);
//            umlAnnotations.add(umlAnnotation);
//        }
//
//        return umlAnnotations;
//    }
//
//
//    private static List<UMLModifier> extractModifiers(LangSingleVariableDeclaration param, String sourceFolder, String filePath) {
//        List<UMLModifier> modifiers = new ArrayList<>();
//
//        // Handle varargs parameters (*args, **kwargs)
//        if (param.isVarArgs()) {
//            UMLModifier varargsModifier = new UMLModifier(
//                    param.getRootCompilationUnit(),
//                    sourceFolder,
//                    filePath,
//                    "varargs",
//                    param
//            );
//            modifiers.add(varargsModifier);
//        }
//
//        // Handle parameters with type annotations
//        if (param.isHasTypeAnnotation()) {
//            UMLModifier typedModifier = new UMLModifier(
//                    param.getRootCompilationUnit(),
//                    sourceFolder,
//                    filePath,
//                    "typed",
//                    param
//            );
//            modifiers.add(typedModifier);
//        }
//
//        // Handle attribute parameters (for dataclass-style parameters)
//        if (param.isAttribute()) {
//            UMLModifier attributeModifier = new UMLModifier(
//                    param.getRootCompilationUnit(),
//                    sourceFolder,
//                    filePath,
//                    "attribute",
//                    param
//            );
//            modifiers.add(attributeModifier);
//        }
//
//        // Handle regular parameters (explicit marking for clarity)
//        if (param.isParameter()) {
//            UMLModifier paramModifier = new UMLModifier(
//                    param.getRootCompilationUnit(),
//                    sourceFolder,
//                    filePath,
//                    "parameter",
//                    param
//            );
//            modifiers.add(paramModifier);
//        }
//
//        return modifiers;
//    }



}
