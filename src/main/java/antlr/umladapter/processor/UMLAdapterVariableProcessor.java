package antlr.umladapter.processor;

import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import gr.uom.java.xmi.LocationInfo;
import gr.uom.java.xmi.UMLParameter;
import gr.uom.java.xmi.UMLType;
import gr.uom.java.xmi.decomposition.VariableDeclaration;

public class UMLAdapterVariableProcessor {

    public static void processVariableDeclarations(LangSingleVariableDeclaration param, UMLParameter umlParam, UMLType typeObject, String sourceFolder, String filePath, LangMethodDeclaration methodDecl){

        String name = param.getLangSimpleName().getIdentifier();
        LocationInfo location = new LocationInfo(sourceFolder, filePath, methodDecl, LocationInfo.CodeElementType.SINGLE_VARIABLE_DECLARATION);

        VariableDeclaration vd = new VariableDeclaration(
                param.getRootCompilationUnit(),
                name,
                typeObject,
                param.isVarArgs(),
                location,
                // TODO
                java.util.Collections.emptyList(),
                java.util.Collections.emptyList()
        );

        vd.setAttribute(param.isAttribute());
        vd.setParameter(param.isParameter());

        umlParam.setVariableDeclaration(vd);
    }
}
