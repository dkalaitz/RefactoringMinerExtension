package antlr.ast.node.declaration;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.node.statement.LangBlock;
import antlr.ast.visitor.LangASTVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class representing a method within a type
public class LangMethodDeclaration extends LangASTNode {
    private String name;
    private List<LangSingleVariableDeclaration> parameters = new ArrayList<>();

    private LangBlock body;

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

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);
        for (LangSingleVariableDeclaration langSingleVariableDeclaration : parameters) {
            langSingleVariableDeclaration.accept(visitor);
        }
        if (body != null) {
            body.accept(visitor);
        }
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

    public String toString() {
        return "LangMethodDeclaration{" +
                "name='" + name + '\'' +
                ", parameters=" + parameters +
                ", body=" + body +
                '}';
    }
}
