package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PyASTBuilder;
import antlr.ast.node.LangASTNode;
import antlr.ast.node.LangASTNodeFactory;
import antlr.ast.node.statement.LangBlock;

import java.util.ArrayList;

public class PyBaseASTBuilder {

    protected final PyASTBuilder mainBuilder;

    public PyBaseASTBuilder(PyASTBuilder mainBuilder) {
        this.mainBuilder = mainBuilder;
    }

    protected LangBlock toBlock(LangASTNode node) {
        /*
         * Helper to safely cast an LangASTNode to a LangBlock. If node is null or not a LangBlock,
         * create an empty LangBlock instead. This helps to handle Python’s indentation blocks.
         */
        if (node instanceof LangBlock) {
            return (LangBlock) node;
        }
        // Fallback: create an empty block or handle error as needed
        return LangASTNodeFactory.createBlock(null, new ArrayList<>());
    }
}