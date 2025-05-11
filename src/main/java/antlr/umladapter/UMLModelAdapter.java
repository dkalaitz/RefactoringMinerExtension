package antlr.umladapter;


import antlr.ast.node.LangASTNode;
import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.declaration.LangTypeDeclaration;

import antlr.ast.node.expression.LangAssignment;
import antlr.ast.node.expression.LangFieldAccess;
import antlr.ast.node.expression.LangInfixExpression;
import antlr.ast.node.expression.LangMethodInvocation;
import antlr.ast.node.statement.LangBlock;
import antlr.ast.node.statement.LangImportStatement;
import antlr.ast.node.statement.LangReturnStatement;
import antlr.ast.node.unit.LangCompilationUnit;
import antlr.base.LangASTUtil;
import gr.uom.java.xmi.*;
import gr.uom.java.xmi.decomposition.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static antlr.umladapter.UMLAdapterUtil.extractUMLImports;
import static antlr.umladapter.processor.UMLAdapterStatementProcessor.*;
import static antlr.umladapter.processor.UMLAdapterVariableProcessor.processVariableDeclarations;

public class UMLModelAdapter {

    private UMLModel umlModel;

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
            System.out.println("AST: " + ast.toString());
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
            System.out.println("Imports: " + imports);

            for (LangTypeDeclaration typeDecl : compilationUnit.getTypes()) {
                UMLClass umlClass = createUMLClass(model, typeDecl, filename, imports);
                model.addClass(umlClass);
            }
        }
    }

    private UMLClass createUMLClass(UMLModel model, LangTypeDeclaration typeDecl, String filename, List<UMLImport> imports) {

        String className = typeDecl.getName();

        String packageName = "";//UMLAdapterUtil.extractPackageName(filename);
        String sourceFolder = "";//UMLAdapterUtil.extractSourceFolder(filename);
        String filepath = "";//UMLAdapterUtil.extractFilePath(filename);


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
        }
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

        UMLType returnType = UMLType.extractTypeObject(methodDecl.getReturnTypeAnnotation());
        UMLParameter returnParam = new UMLParameter("", returnType, "return", false);
        umlOperation.addParameter(returnParam);


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

        return umlOperation;
    }

    private void processMethodBody(LangBlock methodBody, CompositeStatementObject composite,
                                   String sourceFolder, String filePath, UMLOperation container) {
        if (methodBody == null || methodBody.getStatements() == null) {
            System.out.println("WARNING: Method body is null or empty");
            return;
        }

        // First implementation: capture return statements
        for (LangASTNode statement : methodBody.getStatements()) {
            if (statement instanceof LangReturnStatement returnStmt) {
                // Create a StatementObject for each return statement
                processReturnStatement(returnStmt, composite, sourceFolder, filePath, container);
            } else if (statement instanceof LangAssignment langAssignment){
                processAssignment(langAssignment, composite, sourceFolder, filePath, container);
            } else if (statement instanceof LangInfixExpression langInfixExpression){
                processInfixExpression(langInfixExpression, composite, sourceFolder, filePath, container);
            } else if (statement instanceof LangMethodInvocation methodInvocation) {
              //  processMethodInvocation(methodInvocation, composite, sourceFolder, filePath, container);
            } else if (statement instanceof LangFieldAccess fieldAccess) {
                processFieldAccess(fieldAccess, composite, sourceFolder, filePath, container);
            } else {
                // For all other statement types, create a basic statement representation
                // This ensures we have at least some content in the body for comparison
                StatementObject genericStatement = new StatementObject(
                        statement.getRootCompilationUnit(),
                        sourceFolder,
                        filePath,
                        statement,
                        composite.getDepth() + 1,
                        LocationInfo.CodeElementType.EXPRESSION_STATEMENT,
                        container,
                        null
                );
                composite.addStatement(genericStatement);
            }
        }
    }


    public UMLModel getUMLModel() {
        return umlModel;
    }
}