package antlr.ast.node.literal;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.NodeTypeEnum;
import antlr.ast.node.PositionInfo;
import antlr.ast.visitor.LangASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class LangListLiteral extends LangASTNode {
    private List<LangASTNode> elements;

    public LangListLiteral() {super(NodeTypeEnum.LIST_LITERAL);}

    public LangListLiteral(PositionInfo positionInfo, List<LangASTNode> elements) {
        super(NodeTypeEnum.LIST_LITERAL, positionInfo);
        this.elements = elements;
    }

    public LangListLiteral(int startLine, int startChar, int endLine, int endChar, int startColumn, int endColumn) {
        super(NodeTypeEnum.LIST_LITERAL, startLine, startChar, endLine, endChar, startColumn, endColumn);
        this.elements = new ArrayList<>();
    }

    @Override
    public void accept(LangASTVisitor visitor) {
        visitor.visit(this);

        // Visit all child elements
        for (LangASTNode element : elements) {
            element.accept(visitor);
        }
    }

    public void addElement(LangASTNode element) {
        if (this.elements == null) {
            this.elements = new ArrayList<>();
        }
        this.elements.add(element);
        addChild(element);
    }

    public List<LangASTNode> getElements() {
        return elements;
    }

    public void setElements(List<LangASTNode> elements) {
        this.elements = elements;

        // Set parent-child relationships for each element
        for (LangASTNode element : elements) {
            addChild(element);
        }
    }

    public String toString() {
        return "LangListLiteral{" +
                "elements=" + elements +
                '}';
    }

}