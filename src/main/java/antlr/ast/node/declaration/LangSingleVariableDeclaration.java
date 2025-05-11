package antlr.ast.node.declaration;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.node.TypeObjectEnum;
import antlr.ast.node.expression.LangSimpleName;
import antlr.ast.visitor.LangASTVisitor;

/**
 * Represents a method/function parameter declaration in a JDT-like style
 */
public class LangSingleVariableDeclaration extends LangASTNode {
    private LangSimpleName langSimpleName;
    private TypeObjectEnum typeAnnotation; // Add this field for Python type hints
    private boolean hasTypeAnnotation = false; // Add this to track if type hint exists
    private boolean isVarArgs = false; // TODO: Handle var args
    private boolean isAttribute = false;
    private boolean isParameter = false;

    public LangSingleVariableDeclaration() {super(NodeTypeEnum.SINGLE_VARIABLE_DECLARATION);}

    public LangSingleVariableDeclaration(LangSimpleName langSimpleName, PositionInfo positionInfo) {
        super(NodeTypeEnum.SINGLE_VARIABLE_DECLARATION, positionInfo);
        this.langSimpleName = langSimpleName;
    }

    public LangSingleVariableDeclaration(LangSimpleName langSimpleName, int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn) {
        super(NodeTypeEnum.SINGLE_VARIABLE_DECLARATION, startLine, startChar, endLine, endChar, startColumn, endColumn);
        this.langSimpleName = langSimpleName;
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    public LangSimpleName getLangSimpleName() {
        return langSimpleName;
    }

    public void setLangSimpleName(LangSimpleName langSimpleName) {
        this.langSimpleName = langSimpleName;
    }

    public TypeObjectEnum getTypeAnnotation() {
        return typeAnnotation;
    }

    public void setTypeAnnotation(TypeObjectEnum typeAnnotation) {
        this.typeAnnotation = typeAnnotation;
    }

    public boolean isHasTypeAnnotation() {
        return hasTypeAnnotation;
    }

    public void setHasTypeAnnotation(boolean hasTypeAnnotation) {
        this.hasTypeAnnotation = hasTypeAnnotation;
    }

    public boolean isVarArgs() {
        return isVarArgs;
    }

    public void setVarArgs(boolean varArgs) {
        isVarArgs = varArgs;
    }

    public boolean isAttribute() {
        return isAttribute;
    }

    public void setAttribute(boolean attribute) {
        isAttribute = attribute;
    }

    public boolean isParameter() {
        return isParameter;
    }

    public void setParameter(boolean parameter) {
        isParameter = parameter;
    }

    public String toString() {
        return "LangSingleVariableDeclaration{" +
                "langSimpleName='" + langSimpleName + '\'' +
                "isParameter='" + isParameter + '\'' +
                "isVarArgs='" + isVarArgs + '\'' +
                '}';
    }
}
