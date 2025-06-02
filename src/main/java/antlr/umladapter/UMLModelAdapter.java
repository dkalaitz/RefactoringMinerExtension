package antlr.umladapter;


import antlr.ast.node.LangASTNode;
import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.declaration.LangTypeDeclaration;

import antlr.ast.node.expression.LangAssignment;
import antlr.ast.node.expression.LangFieldAccess;
import antlr.ast.node.expression.LangSimpleName;
import antlr.ast.node.metadata.comment.LangComment;
import antlr.ast.node.statement.LangBlock;
import antlr.ast.node.statement.LangExpressionStatement;
import antlr.ast.node.unit.LangCompilationUnit;
import antlr.base.LangASTUtil;
import gr.uom.java.xmi.*;
import gr.uom.java.xmi.decomposition.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.logging.Logger;

import static antlr.ast.visitor.LangVisitor.stringify;
import static antlr.umladapter.UMLAdapterUtil.*;
import static antlr.umladapter.processor.UMLAdapterStatementProcessor.*;
import static antlr.umladapter.processor.UMLAdapterVariableProcessor.processVariableDeclarations;

public class UMLModelAdapter {

    private UMLModel umlModel;
    private static final Logger LOGGER = Logger.getLogger(UMLModelAdapter.class.getName());


    public UMLModelAdapter(Map<String, String> pythonFiles) throws IOException {
        // Parse Python files to custom AST
        Map<String, LangASTNode> pythonASTMap = parsePythonFiles(pythonFiles);

        // Create UML model directly from custom AST
        umlModel = createUMLModel(pythonASTMap);
    }

    private Map<String, LangASTNode> parsePythonFiles(Map<String, String> pythonFiles) throws IOException {
        Map<String, LangASTNode> result = new HashMap<>();
        for (Map.Entry<String, String> entry : pythonFiles.entrySet()) {
            LangASTNode ast = LangASTUtil.getCustomPythonAST(
                    new StringReader(entry.getValue()));
            System.out.print("AST Structure: " + ast.toString());
            result.put(entry.getKey(), ast);
        }

        return result;
    }

    private UMLModel createUMLModel(Map<String, LangASTNode> pythonASTMap) {
        UMLModel model = new UMLModel(Collections.emptySet());

        // Process each Python AST and populate the UML model
        for (Map.Entry<String, LangASTNode> entry : pythonASTMap.entrySet()) {
            String filename = entry.getKey();
            LangASTNode ast = entry.getValue();

            // Extract UML entities from AST
            extractUMLEntities(ast, model, filename);
        }

        return model;
    }

    private void extractUMLEntities(LangASTNode ast, UMLModel model, String filename) {
        if (ast instanceof LangCompilationUnit compilationUnit) {
            // Process imports
            List<UMLImport> imports = extractUMLImports(compilationUnit, filename);

            for (LangTypeDeclaration typeDecl : compilationUnit.getTypes()) {
                UMLClass umlClass = createUMLClass(model, typeDecl, filename, imports);
                model.addClass(umlClass);
            }

            // Handle top level methods
            List<LangMethodDeclaration> topLevelMethods = compilationUnit.getTopLevelMethods();
            UMLClass moduleClass = createModuleClass(compilationUnit, filename, imports);
            String sourceFolder = UMLAdapterUtil.extractSourceFolder(filename);
            String filepath = UMLAdapterUtil.extractFilePath(filename);

            moduleClass.setActualSignature(moduleClass.getName());
            moduleClass.setVisibility(Visibility.PUBLIC);
            moduleClass.setAbstract(false);
            moduleClass.setInterface(false);
            moduleClass.setFinal(false);
            moduleClass.setStatic(false);
            moduleClass.setAnnotation(false);
            moduleClass.setEnum(false);
            moduleClass.setRecord(false);

            if (!topLevelMethods.isEmpty()) {
                for (LangMethodDeclaration method : topLevelMethods) {
                    UMLOperation operation = createUMLOperation(method, moduleClass.getName(),
                            sourceFolder, filepath);
                    moduleClass.addOperation(operation);
                }
            }
            model.addClass(moduleClass);
        }
    }

    private UMLClass createModuleClass(LangCompilationUnit compilationUnit, String filename, List<UMLImport> imports) {
        String moduleName = UMLAdapterUtil.extractModuleName(filename);
        String packageName = UMLAdapterUtil.extractPackageName(filename);
        String sourceFolder = UMLAdapterUtil.extractSourceFolder(filename);
        String filepath = UMLAdapterUtil.extractFilePath(filename);

        LocationInfo locationInfo = new LocationInfo(sourceFolder, filepath, compilationUnit,
                LocationInfo.CodeElementType.TYPE_DECLARATION);

        UMLClass moduleClass = new UMLClass(packageName, moduleName, locationInfo, true, imports);
        moduleClass.setStatic(true); // Mark as a module-level class

        return moduleClass;
    }


    private UMLClass createUMLClass(UMLModel model, LangTypeDeclaration typeDecl, String filename, List<UMLImport> imports) {

        String className = typeDecl.getName();

        String packageName = UMLAdapterUtil.extractPackageName(filename);
        String sourceFolder = UMLAdapterUtil.extractSourceFolder(filename);
        String filepath = UMLAdapterUtil.extractFilePath(filename);


        // TODO: Handle qualified names for classes that are in the same file. Add class name in the qualified name

        LocationInfo locationInfo = new LocationInfo(sourceFolder,
                filepath,
                typeDecl,
                LocationInfo.CodeElementType.TYPE_DECLARATION);

        UMLClass umlClass = new UMLClass(packageName, className, locationInfo, typeDecl.isTopLevel(), imports);

        if (!typeDecl.getSuperClassNames().isEmpty()) {
            // Set the first superclass as the main superclass
            String primarySuperClass = typeDecl.getSuperClassNames().get(0);
            UMLType superClassType = UMLType.extractTypeObject(primarySuperClass);
            umlClass.setSuperclass(superClassType);
            model.addGeneralization(new UMLGeneralization(umlClass, typeDecl.getSuperClassNames().get(0)));

            // Add remaining superclasses as interfaces
            for (int i = 1; i < typeDecl.getSuperClassNames().size(); i++) {
                String additionalSuperClass = typeDecl.getSuperClassNames().get(i);
                UMLType interfaceType = UMLType.extractTypeObject(additionalSuperClass);
                umlClass.addImplementedInterface(interfaceType);
                model.addRealization(new UMLRealization(umlClass, typeDecl.getSuperClassNames().get(i)));
            }
        }

        // TODO: (Optional/Future) Consider mapping type parameters, nested types, fields/attributes, javadoc, annotations, and comments

        // Setters
        umlClass.setActualSignature(typeDecl.getActualSignature());
        umlClass.setVisibility(typeDecl.getVisibility());
        umlClass.setAbstract(typeDecl.isAbstract());
        umlClass.setInterface(typeDecl.isInterface());
        umlClass.setFinal(typeDecl.isFinal());
        umlClass.setStatic(typeDecl.isStatic());
        umlClass.setAnnotation(typeDecl.isAnnotation());
        umlClass.setEnum(typeDecl.isEnum());
        umlClass.setRecord(typeDecl.isRecord());

        for (LangMethodDeclaration methodDecl : typeDecl.getMethods()) {
            UMLOperation umlOperation = createUMLOperation(methodDecl, className, sourceFolder, filepath);
            umlClass.addOperation(umlOperation);
            if ("__init__".equals(methodDecl.getName())) {
                List<UMLAttribute> attributes = getAttributes(methodDecl, sourceFolder, filepath);
                for (UMLAttribute attribute : attributes) {
                    attribute.setClassName(className);
                    umlClass.addAttribute(attribute);
                }
                break;
            }
        }
        logUMLClass(umlClass);
        return umlClass;
    }

    private UMLOperation createUMLOperation(LangMethodDeclaration methodDecl, String className, String sourceFolder, String filePath) {

        LocationInfo locationInfo = new LocationInfo(sourceFolder, filePath, methodDecl, LocationInfo.CodeElementType.METHOD_DECLARATION);

        String operationName = methodDecl.getCleanName();
        UMLOperation umlOperation = new UMLOperation(operationName, locationInfo);
        umlOperation.setClassName(className);
        List<LangSingleVariableDeclaration> params = methodDecl.getParameters();
        List<String> parameterNames = new ArrayList<>();

        // SKIP "self" if present as first parameter
        int paramOffset = 0;
        if (!params.isEmpty() && "self".equals(params.get(0).getLangSimpleName().getIdentifier())) {
            paramOffset = 1;
        }

        for (int i = paramOffset; i < params.size(); i++) {
            LangSingleVariableDeclaration param = params.get(i);
            UMLType typeObject = UMLType.extractTypeObject(param.getTypeAnnotation().getName());
            UMLParameter umlParam = new UMLParameter(param.getLangSimpleName().getIdentifier(), typeObject, "parameter", param.isVarArgs()); // TODO: Handle var args
            processVariableDeclarations(param, umlParam, typeObject, sourceFolder, filePath, methodDecl);
            umlOperation.addParameter(umlParam);
            parameterNames.add(param.getLangSimpleName().getIdentifier());
        }

        umlOperation.setFinal(methodDecl.isFinal());
        umlOperation.setStatic(methodDecl.isStatic());
        umlOperation.setConstructor(methodDecl.isConstructor());
        umlOperation.setVisibility(methodDecl.getVisibility());
        umlOperation.setAbstract(methodDecl.isAbstract());
        umlOperation.setNative(methodDecl.isNative());
        umlOperation.setSynchronized(methodDecl.isSynchronized());
        umlOperation.setActualSignature(methodDecl.getActualSignature());

        processComments(methodDecl, sourceFolder, filePath, umlOperation);

        UMLType returnType = UMLType.extractTypeObject(methodDecl.getReturnTypeAnnotation());
        UMLParameter returnParam = new UMLParameter("", returnType, "return", false);
        umlOperation.addParameter(returnParam);
        // Create a dummy attribute for testing
        List<UMLAttribute> dummyAttributes = Arrays.asList(
                new UMLAttribute("self", UMLType.extractTypeObject("object"), null)
        );

       // LOGGER.info("\n\nAttributes: " + attributes);

        OperationBody opBody = new OperationBody(
                methodDecl.getRootCompilationUnit(),
                sourceFolder,
                filePath,
                methodDecl.getBody(),
                umlOperation,
                new ArrayList<>()
        );

        // CRITICAL: Process the method body statements to populate the CompositeStatementObject
        processMethodBody(methodDecl.getBody(), opBody.getCompositeStatement(), sourceFolder, filePath, umlOperation);

        umlOperation.setBody(opBody);

        logUMLOperation(umlOperation, methodDecl);

        return umlOperation;
    }

    private List<UMLAttribute> getAttributes(LangMethodDeclaration methodDecl, String sourceFolder, String filePath) {
        List<UMLAttribute> attributes = new ArrayList<>();

        // Only process __init__ method for attribute extraction
        if (!"__init__".equals(methodDecl.getName())) {
            return attributes;
        }

        LangBlock methodBody = methodDecl.getBody();
        if (methodBody == null) {
            return attributes;
        }

        if (methodBody.getStatements() != null) {
            for (LangASTNode statement : methodBody.getStatements()) {
                // Handle direct assignments
                if (statement instanceof LangAssignment assignment) {
                    processAssignmentForAttribute(methodDecl, assignment, attributes, sourceFolder, filePath);
                }
                // Handle expression statements that contain assignments
                else if (statement instanceof LangExpressionStatement exprStmt) {
                    if (exprStmt.getExpression() instanceof LangAssignment assignment) {
                        processAssignmentForAttribute(methodDecl, assignment, attributes, sourceFolder, filePath);
                    }
                }
            }
        }


        return attributes;
    }

    private void processAssignmentForAttribute(LangMethodDeclaration methodDeclaration, LangAssignment assignment, List<UMLAttribute> attributes,
                                               String sourceFolder, String filePath) {
        LangASTNode leftSide = assignment.getLeftSide();

        if (leftSide instanceof LangFieldAccess langFieldAccess) {
            LangASTNode expression = langFieldAccess.getExpression();

            // Check if it's self.attribute
            if (expression instanceof LangSimpleName simpleName) {
                if ("self".equals(simpleName.getIdentifier())) {
                    String attributeName = langFieldAccess.getName().getIdentifier();

                    // Create VariableDeclaration for the attribute
                    // Pass the assignment node so extractInitializer can get the right-hand side
                    // TODO
                    VariableDeclaration variableDeclaration = new VariableDeclaration(
                            assignment.getRootCompilationUnit(),
                            sourceFolder,
                            filePath,
                            assignment,
                            null,
                            attributeName
                    );

                    variableDeclaration.setAttribute(true);

                    // Create UMLAttribute
                    UMLAttribute attribute = new UMLAttribute(
                            attributeName,
                            UMLType.extractTypeObject("Object"),
                            new LocationInfo(
                                    assignment.getRootCompilationUnit(),
                                    sourceFolder,
                                    filePath,
                                    langFieldAccess,
                                    LocationInfo.CodeElementType.FIELD_DECLARATION
                            )
                    );

                    // Set the variable declaration on the attribute
                    attribute.setVariableDeclaration(variableDeclaration);
                    // TODO
                    attribute.setVisibility(Visibility.PUBLIC);
                    attribute.setFinal(false);
                    attribute.setStatic(false);
                    attribute.setClassName(methodDeclaration.getName());

                    attributes.add(attribute);

                    LOGGER.info("Created attribute: " + attributeName + " with initializer: " +
                            (variableDeclaration.getInitializer() != null ? "yes" : "no"));
                }
            }
        }
    }



    private void processMethodBody(LangBlock methodBody, CompositeStatementObject composite,
                                   String sourceFolder, String filePath, UMLOperation container) {
        if (methodBody == null || methodBody.getStatements() == null) {
            System.out.println("WARNING: Method body is null or empty");
            return;
        }

        for (LangASTNode statement : methodBody.getStatements()) {
            processStatement(statement, composite, sourceFolder, filePath, container);
        }

    }

    private void processComments(LangMethodDeclaration methodDecl, String sourceFolder, String filePath, UMLOperation umlOperation){
        List<UMLComment> comments = new ArrayList<>();
        for (LangComment langComment: methodDecl.getComments()) {
            if (langComment.isBlockComment() || langComment.isDocComment()){
                comments.add(new UMLComment(langComment.getContent(), new LocationInfo(
                        methodDecl.getRootCompilationUnit(),
                        sourceFolder,
                        filePath,
                        langComment,
                        LocationInfo.CodeElementType.BLOCK_COMMENT
                )));
            } else if (langComment.isLineComment()){
                comments.add(new UMLComment(langComment.getContent(), new LocationInfo(
                        methodDecl.getRootCompilationUnit(),
                        sourceFolder,
                        filePath,
                        langComment,
                        LocationInfo.CodeElementType.LINE_COMMENT
                )));
            }
        }
        umlOperation.setComments(comments);
    }

    private void logUMLClass(UMLClass umlClass) {
        StringBuilder sb = new StringBuilder();
        sb.append("UMLClass created: ").append(umlClass)
                .append("\nName: ").append(umlClass.getName())
                .append("\nPackage: ").append(umlClass.getPackageName())
                .append("\nSource Folder: ").append(umlClass.getLocationInfo().getSourceFolder())
                .append("\nFile Path: ").append(umlClass.getLocationInfo().getFilePath())
                .append("\nActual Signature: ").append(umlClass.getActualSignature())
                .append("\nNon Qualified Name: ").append(umlClass.getNonQualifiedName())
                .append("\nVisibility: ").append(umlClass.getVisibility())
                .append("\nIs Interface: ").append(umlClass.isInterface())
                .append("\nIs Abstract: ").append(umlClass.isAbstract())
                .append("\nSuperclass: ").append(umlClass.getSuperclass())
                .append("\nImplemented Interfaces: ").append(umlClass.getImplementedInterfaces())
                .append("\nAttributes: ").append(umlClass.getAttributes())
                .append("\nOperations (methods): \n");

        for (UMLOperation op : umlClass.getOperations()) {
            sb.append("  - ").append(op.getActualSignature())
                    .append(" [Visibility: ").append(op.getVisibility())
                    .append(", Parameters: ").append(op.getParameters())
                    .append("]\n");
        }

        sb.append("\n");

        LOGGER.info(sb.toString());
    }

    private void logUMLOperation(UMLOperation umlOperation, LangMethodDeclaration methodDecl){
        String bodyString = stringify(methodDecl.getBody());
        LOGGER.info(
                "UMLOperation created: " + umlOperation +
                        "\nSignature: " + umlOperation.getActualSignature() +
                        "\nQualified Name: " + umlOperation.getClassName() + "." + umlOperation.getName() +
                        "\nName: " + umlOperation.getName() +
                        "\nClass: " + umlOperation.getClassName() +
                        "\nVisibility: " + umlOperation.getVisibility() +
                        "\nParameters: " + umlOperation.getParameters() +
                        "\nIs Constructor: " + umlOperation.isConstructor() +
                        "\nIs Final: " + umlOperation.isFinal() +
                        "\nIs Static: " + umlOperation.isStatic() +
                        "\nIs Abstract: " + umlOperation.isAbstract() +
                        "\nIs Native: " + umlOperation.isNative() +
                        "\nReturn Type: " + umlOperation.getReturnParameter() +
                        "\nBody Hash Code: " + umlOperation.getBody().getBodyHashCode() +
                        "\nMethod body stringify result for " + methodDecl.getName() + ": " + bodyString +
                        "\nString hash: " + bodyString.hashCode() +
                        "\n\n"
        );

    }



    public UMLModel getUMLModel() {
        return umlModel;
    }
}