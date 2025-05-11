package antlr.ast.node.unit;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.node.metadata.comment.LangComment;
import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.ast.node.statement.LangImportStatement;
import antlr.ast.visitor.LangASTVisitor;

import java.util.ArrayList;
import java.util.List;

// Class representing the entire source file (LangCompilationUnit)
public class LangCompilationUnit extends LangASTNode {
    private List<LangTypeDeclaration> types = new ArrayList<>();
    private List<LangMethodDeclaration> topLevelMethods = new ArrayList<>();
    private List<LangASTNode> topLevelStatements = new ArrayList<>();
    private List<LangImportStatement> imports = new ArrayList<>();
    private List<LangComment> comments = new ArrayList<>();
    private String moduleName;

    public LangCompilationUnit() {super(NodeTypeEnum.COMPILATION_UNIT);}

    public LangCompilationUnit(PositionInfo positionInfo) {
        super(NodeTypeEnum.COMPILATION_UNIT, positionInfo);
    }

    public LangCompilationUnit(int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn) {
        super(NodeTypeEnum.COMPILATION_UNIT, startLine, startChar, endLine, endChar, startColumn, endColumn);
    }

    public void addImport(LangImportStatement importStmt) {
        imports.add(importStmt);
        addChild(importStmt);
    }

    public void addType(LangTypeDeclaration type) {
        types.add(type);
        addChild(type);
    }

    public void addMethod(LangMethodDeclaration method) {
        method.setStatic(true);
        topLevelMethods.add(method);
        addChild(method);
    }

    public void addStatement(LangASTNode statement) {
        topLevelStatements.add(statement);
        addChild(statement);
    }

    public void addComment(LangComment comment) {
        comments.add(comment);
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);

        // Visit types
        for (LangTypeDeclaration type : types) {
            type.accept(visitor);
        }

        // Visit top-level methods
        for (LangMethodDeclaration method : topLevelMethods) {
            method.accept(visitor);
        }

        // Visit top-level statements
        for (LangASTNode statement : topLevelStatements) {
            statement.accept(visitor);
        }

        for (LangComment comment : comments) {
            comment.accept(visitor);
        }

    }


    public List<LangTypeDeclaration> getTypes() {
        return types;
    }

    public void setTypes(List<LangTypeDeclaration> types) {
        this.types = types;
    }

    public List<LangMethodDeclaration> getTopLevelMethods() {
        return topLevelMethods;
    }

    public void setTopLevelMethods(List<LangMethodDeclaration> topLevelMethods) {
        this.topLevelMethods = topLevelMethods;
    }

    public List<LangASTNode> getTopLevelStatements() {
        return topLevelStatements;
    }

    public void setTopLevelStatements(List<LangASTNode> topLevelStatements) {
        this.topLevelStatements = topLevelStatements;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public List<LangImportStatement> getImports() {
        return imports;
    }

    public void setImports(List<LangImportStatement> imports) {
        this.imports = imports;
    }

    public List<LangComment> getComments() {
        return comments;
    }

    public void setComments(List<LangComment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "LangCompilationUnit{" +
                "types=" + types +
                ", topLevelMethods=" + topLevelMethods +
                ", topLevelStatements=" + topLevelStatements +
                ", imports=" + imports +
                ", comments=" + comments +
                ", moduleName='" + moduleName + '\'' +
                '}';
    }
}