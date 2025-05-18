package antlr.ast.node.declaration;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.node.metadata.LangAnnotation;
import antlr.ast.node.metadata.comment.LangComment;
import antlr.ast.node.statement.LangBlock;
import antlr.ast.visitor.LangASTVisitor;
import gr.uom.java.xmi.Visibility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class representing a method within a type
public class LangMethodDeclaration extends LangASTNode {
    private String name;
    private String cleanName;
    private List<LangSingleVariableDeclaration> parameters = new ArrayList<>();
    private LangBlock body;
    private Visibility visibility;
    private boolean isStatic = false; // TODO: Top Level Methods are static!
    private boolean isConstructor = false;
    private boolean isAbstract = false;
    private boolean isFinal = false;
    private boolean isNative = false;
    private boolean isSynchronized = false;
    private String actualSignature;
    private String returnTypeAnnotation; // Add this field for Python type hints
    private List<LangAnnotation> langAnnotations = new ArrayList<>();
    private List<LangComment> comments = new ArrayList<>();


    public LangMethodDeclaration() {super(NodeTypeEnum.METHOD_DECLARATION);}

    public LangMethodDeclaration(PositionInfo positionInfo) {
        super(NodeTypeEnum.METHOD_DECLARATION, positionInfo);
    }

    public LangMethodDeclaration(int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn) {
        super(NodeTypeEnum.METHOD_DECLARATION, startLine, startChar, endLine, endChar, startColumn, endColumn);
    }

    public void addParameter(LangSingleVariableDeclaration langSingleVariableDeclaration) {
        parameters.add(langSingleVariableDeclaration);
        addChild(langSingleVariableDeclaration);
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

    public LangBlock getBody() {
        return body;
    }

    public void setBody(LangBlock body) {
        this.body = body;
        addChild(body);
    }

    public List<LangSingleVariableDeclaration> getParameters() {
        return parameters;
    }

    public void setParameters(List<LangSingleVariableDeclaration> parameters) {
        this.parameters = parameters;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public void setConstructor(boolean constructor) {
        isConstructor = constructor;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public boolean isNative() {
        return isNative;
    }

    public void setNative(boolean aNative) {
        isNative = aNative;
    }

    public boolean isSynchronized() {
        return isSynchronized;
    }

    public void setSynchronized(boolean aSynchronized) {
        isSynchronized = aSynchronized;
    }

    public String getActualSignature() {
        return actualSignature;
    }

    public void setActualSignature(String actualSignature) {
        this.actualSignature = actualSignature;
    }

    public String getReturnTypeAnnotation() {
        return returnTypeAnnotation;
    }

    public void setReturnTypeAnnotation(String returnTypeAnnotation) {
        this.returnTypeAnnotation = returnTypeAnnotation;
    }

    public String getCleanName() {
        return cleanName;
    }

    public void setCleanName(String cleanName) {
        this.cleanName = cleanName;
    }

    public List<LangAnnotation> getLangAnnotations() {
        return langAnnotations;
    }

    public void setLangAnnotations(List<LangAnnotation> langAnnotations) {
        this.langAnnotations = langAnnotations;
    }

    public List<LangComment> getComments() {
        return comments;
    }

    public void setComments(List<LangComment> comments) {
        this.comments = comments;
    }

    public String toString() {
        return "LangMethodDeclaration{" +
                "name='" + name + '\'' +
                ", parameters=" + parameters +
                ", body=" + body +
                ", visibility=" + visibility +
                ", cleanName=" + cleanName +
                ", returnType=" + returnTypeAnnotation +
                '}';
    }
}
