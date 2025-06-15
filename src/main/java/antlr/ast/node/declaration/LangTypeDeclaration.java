package antlr.ast.node.declaration;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.node.metadata.comment.LangComment;
import antlr.ast.visitor.LangASTVisitor;
import gr.uom.java.xmi.Visibility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class representing a type (e.g., class or interface)
public class LangTypeDeclaration extends LangDeclaration {
    private String name;
    private List<LangMethodDeclaration> methods = new ArrayList<>();
    private List<String> superClassNames = new ArrayList<>();
    private Visibility visibility;
    private boolean isAbstract = false;
    private boolean isInterface = false;
    private boolean isFinal = false;
    private boolean isStatic = false;
    private boolean isEnum = false;
    private boolean isAnnotation = false;
    private boolean isSealed = false;
    private boolean isRecord = false;
    private boolean isTopLevel = false;
    private String actualSignature;
    List<LangComment> comments = new ArrayList<>();

    public LangTypeDeclaration() {super(NodeTypeEnum.TYPE_DECLARATION);}

    public LangTypeDeclaration(PositionInfo positionInfo) {
        super(NodeTypeEnum.TYPE_DECLARATION, positionInfo);
    }

    public LangTypeDeclaration(int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn) {
        super(NodeTypeEnum.TYPE_DECLARATION, startLine, startChar, endLine, endChar, startColumn, endColumn);
    }

    public LangTypeDeclaration(NodeTypeEnum nodeType, PositionInfo positionInfo, String name, List<LangMethodDeclaration> methods, Visibility visibility, boolean isAbstract, boolean isInterface, boolean isFinal, boolean isStatic, boolean isEnum, boolean isAnnotation, boolean isSealed, boolean isRecord, boolean isTopLevel, String actualSignature) {
        super(nodeType, positionInfo);
        this.name = name;
        this.methods = methods;
        this.visibility = visibility;
        this.isAbstract = isAbstract;
        this.isInterface = isInterface;
        this.isFinal = isFinal;
        this.isStatic = isStatic;
        this.isEnum = isEnum;
        this.isAnnotation = isAnnotation;
        this.isSealed = isSealed;
        this.isRecord = isRecord;
        this.isTopLevel = isTopLevel;
        this.actualSignature = actualSignature;
    }

    public void addMethod(LangMethodDeclaration method) {
        methods.add(method);
        addChild(method);
    }

    public void addComment(LangComment comment) {
        comments.add(comment);
        addChild(comment);
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LangMethodDeclaration> getMethods() {
        return methods;
    }

    public void setMethods(List<LangMethodDeclaration> methods) {
        this.methods = methods;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public void setInterface(boolean anInterface) {
        isInterface = anInterface;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public boolean isEnum() {
        return isEnum;
    }

    public void setEnum(boolean anEnum) {
        isEnum = anEnum;
    }

    public boolean isAnnotation() {
        return isAnnotation;
    }

    public void setAnnotation(boolean annotation) {
        isAnnotation = annotation;
    }

    public boolean isSealed() {
        return isSealed;
    }

    public void setSealed(boolean sealed) {
        isSealed = sealed;
    }

    public boolean isRecord() {
        return isRecord;
    }

    public void setRecord(boolean record) {
        isRecord = record;
    }

    public boolean isTopLevel() {
        return isTopLevel;
    }

    public void setTopLevel(boolean topLevel) {
        isTopLevel = topLevel;
    }

    public String getActualSignature() {
        return actualSignature;
    }

    public void setActualSignature(String actualSignature) {
        this.actualSignature = actualSignature;
    }

    public List<String> getSuperClassNames() {
        return superClassNames;
    }

    public void setSuperClassNames(List<String> superClassNames) {
        this.superClassNames = superClassNames;
    }

    public String toString() {
        return "LangTypeDeclaration{" +
                "name='" + name + '\'' +
                ", methods=" + methods +
                '}';
    }
}