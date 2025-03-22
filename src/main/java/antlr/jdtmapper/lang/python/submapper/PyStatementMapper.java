package antlr.jdtmapper.lang.python.submapper;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.expression.LangAssignment;
import antlr.ast.node.statement.LangBlock;
import antlr.ast.node.statement.LangReturnStatement;
import antlr.jdtmapper.lang.python.PyJdtASTMapper;
import org.eclipse.jdt.core.dom.*;

/**
 * Maps Python statement nodes to JDT statement nodes
 */
public class PyStatementMapper {

    /**
     * Helper method to add a statement to a block.
     *
     * @param block The block to add the statement to
     * @param stmt The statement to add
     */
    @SuppressWarnings("unchecked")
    private void addStatement(Block block, Statement stmt) {
        block.statements().add(stmt);
    }

    /**
     * Maps a LangBlock node to a JDT Block node.
     *
     * @param langBlock The LangBlock node to map
     * @param jdtAst The JDT AST to create nodes with
     * @param pyJdtASTMapper Reference to the main mapper for delegating child node mapping
     * @return A JDT Block node
     */
    public Block mapBlock(LangBlock langBlock, AST jdtAst, PyJdtASTMapper pyJdtASTMapper) {
        if (langBlock == null) return null;

        Block block = jdtAst.newBlock();

        // Map statements
        if (langBlock.getStatements() != null) {
            System.out.println("Python block has " + langBlock.getStatements().size() + " statements");

            for (LangASTNode langStmt : langBlock.getStatements()) {
                System.out.println("Processing Python statement: " + langStmt.getClass().getSimpleName());

                // Use the main mapper to convert LangASTNode to JDT ASTNode
                ASTNode jdtNode = pyJdtASTMapper.map(langStmt, jdtAst);

                // Check if the mapped node is a valid Statement
                if (jdtNode instanceof Statement) {
                    addStatement(block, (Statement) jdtNode);
                } else if (jdtNode instanceof Expression) {
                    // Wrap expressions in ExpressionStatement
                    ExpressionStatement expressionStatement = jdtAst.newExpressionStatement((Expression) jdtNode);
                    addStatement(block, expressionStatement);
                }

            }
        }

        return block;
    }


    /**
     * Maps a LangReturnStatement node to a JDT ReturnStatement node.
     *
     * @param langReturn The LangReturnStatement node to map
     * @param jdtAst The JDT AST to create nodes with
     * @param pyJdtASTMapper Reference to the main mapper for delegating child node mapping
     * @return A JDT ReturnStatement node
     */
    public ReturnStatement mapReturnStatement(LangReturnStatement langReturn, AST jdtAst, PyJdtASTMapper pyJdtASTMapper) {
        if (langReturn == null) return null;

        ReturnStatement returnStatement = jdtAst.newReturnStatement();

        LangASTNode langExpression = langReturn.getExpression();
        if (langExpression != null) {
            Expression expression = (Expression) pyJdtASTMapper.map(langExpression, jdtAst);
            returnStatement.setExpression(expression);
        }

        return returnStatement;
    }

}