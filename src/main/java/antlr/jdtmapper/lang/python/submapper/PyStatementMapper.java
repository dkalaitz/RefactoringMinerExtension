package antlr.jdtmapper.lang.python.submapper;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.statement.LangBlock;
import antlr.ast.node.statement.LangExpressionStatement;
import antlr.ast.node.statement.LangReturnStatement;
import antlr.jdtmapper.lang.python.PyJdtASTMapper;
import org.eclipse.jdt.core.dom.*;

import static antlr.jdtmapper.BaseJdtASTMapper.setSourceRange;

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
     * Maps a LangExpressionStatement node to a JDT ExpressionStatement node.
     * Follows the same pattern as other mapper methods.
     *
     * @param langExprStatement The LangExpressionStatement to map
     * @param jdtAst The JDT AST to create nodes with
     * @param pyJdtASTMapper Reference to the main mapper for delegating expression mapping
     * @return A JDT ExpressionStatement node
     */
    public ExpressionStatement mapExpressionStatement(LangExpressionStatement langExprStatement, AST jdtAst, PyJdtASTMapper pyJdtASTMapper) {
        if (langExprStatement == null) return null;

        // First map the contained expression
        Expression mappedExpression = null;

        if (langExprStatement.getExpression() != null) {
            ASTNode node = pyJdtASTMapper.map(langExprStatement.getExpression(), jdtAst);
            if (node instanceof Expression) {
                mappedExpression = (Expression) node;
            } else {
                // Default placeholder if mapping failed
                mappedExpression = jdtAst.newSimpleName("expr");
                System.err.println("Warning: Failed to map expression in statement, using placeholder");
            }
        } else {
            // Default placeholder if expression is null
            mappedExpression = jdtAst.newSimpleName("expr");
            System.err.println("Warning: Expression statement has null expression, using placeholder");
        }

        // Create the expression statement with the already mapped expression
        ExpressionStatement expressionStatement = jdtAst.newExpressionStatement(mappedExpression);

        // Set source range directly from langExprStatement with explicit debug output
        int startPos = langExprStatement.getStartChar();
        int length = langExprStatement.getLength();
        System.out.println("Setting ExpressionStatement source range: start=" + startPos + ", length=" + length);
        expressionStatement.setSourceRange(startPos, length);

        return expressionStatement;
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
        setSourceRange(block, langBlock);

        // Map statements
        if (langBlock.getStatements() != null) {

            for (LangASTNode langStmt : langBlock.getStatements()) {

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
        setSourceRange(returnStatement, langReturn);

        LangASTNode langExpression = langReturn.getExpression();
        if (langExpression != null) {
            Expression expression = (Expression) pyJdtASTMapper.map(langExpression, jdtAst);
            returnStatement.setExpression(expression);
        }

        return returnStatement;
    }

}