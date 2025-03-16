package antlr.jdtmapper;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.ast.node.expression.LangAssignment;
import antlr.ast.node.statement.LangBlock;
import antlr.ast.node.unit.LangCompilationUnit;
import org.eclipse.jdt.core.dom.*;

public abstract class BaseJdtASTMapper implements JdtASTMapper {

    @Override
    public ASTNode map(LangASTNode langASTNode, AST jdtAst) {
        if (langASTNode == null) { return null; }

        if (langASTNode instanceof LangCompilationUnit) {
            return mapCompilationUnit((LangCompilationUnit) langASTNode, jdtAst);
        } else if (langASTNode instanceof LangTypeDeclaration) {
            return mapTypeDeclaration((LangTypeDeclaration) langASTNode, jdtAst);
        } else if (langASTNode instanceof LangMethodDeclaration) {
            return mapMethodDeclaration((LangMethodDeclaration) langASTNode, jdtAst);
        } else if (langASTNode instanceof LangBlock) {
            return mapBlock((LangBlock) langASTNode, jdtAst);
        } else if (langASTNode instanceof LangSingleVariableDeclaration) {
            return mapSingleVariableDeclaration((LangSingleVariableDeclaration) langASTNode, jdtAst);
        } else if (langASTNode instanceof LangAssignment) {
            return mapAssignment((LangAssignment) langASTNode, jdtAst);
        }

        throw new UnsupportedOperationException("Unsupported AST node type: " + langASTNode.getClass());
    }

    public abstract CompilationUnit mapCompilationUnit(LangCompilationUnit langCompilationUnit, AST jdtAst);
    public abstract TypeDeclaration mapTypeDeclaration(LangTypeDeclaration langTypeDeclaration, AST jdtAst);
    public abstract MethodDeclaration mapMethodDeclaration(LangMethodDeclaration langMethodDeclaration, AST jdtAst);
    public abstract SingleVariableDeclaration mapSingleVariableDeclaration(LangSingleVariableDeclaration langSingleVariableDeclaration, AST jdtAst);
    public abstract Block mapBlock(LangBlock langBlock, AST jdtAst);
    public abstract Assignment mapAssignment(LangAssignment langAssignment, AST jdtAst);

    /**
     * Helper method to safely add a type declaration to a CompilationUnit
     */
    @SuppressWarnings("unchecked")
    protected void addTypeToCompilationUnit(CompilationUnit cu, AbstractTypeDeclaration typeDecl) {
        cu.types().add(typeDecl);
    }

    /**
     * Helper method to safely add a modifier to a type declaration
     */
    @SuppressWarnings("unchecked")
    protected void addModifier(TypeDeclaration typeDecl, Modifier modifier) {
        typeDecl.modifiers().add(modifier);
    }

    /**
     * Helper method to safely add a body declaration to a type declaration
     */
    @SuppressWarnings("unchecked")
    protected void addBodyDeclaration(TypeDeclaration typeDecl, BodyDeclaration declaration) {
        typeDecl.bodyDeclarations().add(declaration);
    }

    /**
     * Helper method to safely add a parameter to a method declaration
     */
    @SuppressWarnings("unchecked")
    protected void addParameter(MethodDeclaration methodDecl, SingleVariableDeclaration param) {
        methodDecl.parameters().add(param);
    }

    /**
     * Helper method to safely add a statement to a block
     */
    @SuppressWarnings("unchecked")
    protected void addStatement(Block block, Statement statement) {
        block.statements().add(statement);
    }

    /**
     * Helper method to safely add a modifier to a method declaration
     */
    @SuppressWarnings("unchecked")
    protected void addModifierToMethod(MethodDeclaration methodDecl, Modifier modifier) {
        methodDecl.modifiers().add(modifier);
    }

    /**
     * Maps a string assignment operator to a JDT Assignment.Operator
     * @param operatorString The string representation of the operator
     * @return The corresponding JDT Assignment.Operator
     */
    protected Assignment.Operator mapAssignmentOperator(String operatorString) {
        return switch (operatorString) {
            case "=" -> Assignment.Operator.ASSIGN;
            case "+=" -> Assignment.Operator.PLUS_ASSIGN;
            case "-=" -> Assignment.Operator.MINUS_ASSIGN;
            case "*=" -> Assignment.Operator.TIMES_ASSIGN;
            case "/=" -> Assignment.Operator.DIVIDE_ASSIGN;
            case "%=" -> Assignment.Operator.REMAINDER_ASSIGN;
            case "&=" -> Assignment.Operator.BIT_AND_ASSIGN;
            case "|=" -> Assignment.Operator.BIT_OR_ASSIGN;
            case "^=" -> Assignment.Operator.BIT_XOR_ASSIGN;
            case "<<=" -> Assignment.Operator.LEFT_SHIFT_ASSIGN;
            case ">>=" -> Assignment.Operator.RIGHT_SHIFT_SIGNED_ASSIGN;
            case ">>>=" -> Assignment.Operator.RIGHT_SHIFT_UNSIGNED_ASSIGN;
            default -> Assignment.Operator.ASSIGN; // Default to simple assignment
        };
    }


}
