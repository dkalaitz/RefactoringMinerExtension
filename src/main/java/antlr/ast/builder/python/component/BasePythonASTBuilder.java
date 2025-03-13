package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PythonASTBuilder;
import antlr.ast.node.ASTNode;
import antlr.ast.node.ASTNodeFactory;
import antlr.ast.node.statement.Block;
import antlr.base.python.Python3ParserBaseVisitor;

import java.util.ArrayList;

public class BasePythonASTBuilder {

    protected final PythonASTBuilder mainBuilder;

    public BasePythonASTBuilder(PythonASTBuilder mainBuilder) {
        this.mainBuilder = mainBuilder;
    }

    protected Block toBlock(ASTNode node) {
        /*
         * Helper to safely cast an ASTNode to a Block. If node is null or not a Block,
         * create an empty Block instead. This helps to handle Python’s indentation blocks.
         */
        if (node instanceof Block) {
            return (Block) node;
        }
        // Fallback: create an empty block or handle error as needed
        return ASTNodeFactory.createBlock(null, new ArrayList<>());
    }
}