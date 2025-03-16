package antlr.jdtmapper.lang.python;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.expression.LangAssignment;
import antlr.ast.node.statement.LangBlock;
import antlr.ast.node.unit.LangCompilationUnit;
import antlr.jdtmapper.BaseJdtASTMapper;
import org.eclipse.jdt.core.dom.*;

/**
 * Maps a custom Python AST node to a JDT-compatible AST node.
 * Python is dynamically typed, so we map all variables to Object type.
 */
public class PyJdtASTMapper extends BaseJdtASTMapper {

    public PyJdtASTMapper() {}

    @Override
    public CompilationUnit mapCompilationUnit(LangCompilationUnit langCompilationUnit, AST jdtAst) {
        if (langCompilationUnit == null) return null;

        CompilationUnit cu = jdtAst.newCompilationUnit();

        System.out.println("CompilationUnit: " + langCompilationUnit);

        // Map type declarations (classes)
        if (langCompilationUnit.getTypes() != null) {
            System.out.println("Types: " + langCompilationUnit.getTypes());
            for (LangTypeDeclaration typeDecl : langCompilationUnit.getTypes()) {
                TypeDeclaration jdtType = mapTypeDeclaration(typeDecl, jdtAst);
                if (jdtType != null) {
                    // Use helper method instead of direct add
                    addTypeToCompilationUnit(cu, jdtType);
                }
            }
        }

        return cu;
    }

    @Override
    public TypeDeclaration mapTypeDeclaration(LangTypeDeclaration langTypeDeclaration, AST jdtAst) {
        if (langTypeDeclaration == null) return null;

        TypeDeclaration typeDecl = jdtAst.newTypeDeclaration();

        // Set class name
        typeDecl.setName(jdtAst.newSimpleName(langTypeDeclaration.getName()));

        // Python classes are public by default
        // Use helper method instead of direct add
        addModifier(typeDecl, jdtAst.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));

        // Add methods
        if (langTypeDeclaration.getMethods() != null) {
            for (LangMethodDeclaration methodDecl : langTypeDeclaration.getMethods()) {
                MethodDeclaration jdtMethod = mapMethodDeclaration(methodDecl, jdtAst);
                if (jdtMethod != null) {
                    // Use helper method instead of direct add
                    addBodyDeclaration(typeDecl, jdtMethod);
                }
            }
        }

        return typeDecl;
    }

    @Override
    public MethodDeclaration mapMethodDeclaration(LangMethodDeclaration langMethodDeclaration, AST jdtAst) {
        if (langMethodDeclaration == null) return null;

        MethodDeclaration methodDecl = jdtAst.newMethodDeclaration();

        // Set method name
        methodDecl.setName(jdtAst.newSimpleName(langMethodDeclaration.getName()));

        // Python methods are public by default
        // Instead of: methodDecl.modifiers().add(jdtAst.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
        // Use:
        addModifierToMethod(methodDecl, jdtAst.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));

        // Map parameters
        if (langMethodDeclaration.getParameters() != null) {
            for (LangSingleVariableDeclaration param : langMethodDeclaration.getParameters()) {
                SingleVariableDeclaration jdtParam = mapSingleVariableDeclaration(param, jdtAst);
                if (jdtParam != null) {
                    // Use helper method instead of direct add
                    addParameter(methodDecl, jdtParam);
                }
            }
        }

        // Map body if available
        if (langMethodDeclaration.getBody() != null) {
            methodDecl.setBody(mapBlock(langMethodDeclaration.getBody(), jdtAst));
        }

        // Set return type (in Python all methods return Object unless specified)
        methodDecl.setReturnType2(jdtAst.newSimpleType(jdtAst.newSimpleName("Object")));

        return methodDecl;
    }

    @Override
    public SingleVariableDeclaration mapSingleVariableDeclaration(LangSingleVariableDeclaration langVar, AST jdtAst) {
        if (langVar == null) return null;

        SingleVariableDeclaration varDecl = jdtAst.newSingleVariableDeclaration();

        // Set variable name
        varDecl.setName(jdtAst.newSimpleName(langVar.getSimpleName().getIdentifier()));

        // Python is dynamically typed, so use Object as the type
        varDecl.setType(jdtAst.newSimpleType(jdtAst.newSimpleName("Object")));

        return varDecl;
    }

    @Override
    public Block mapBlock(LangBlock langBlock, AST jdtAst) {
        if (langBlock == null) return null;

        Block block = jdtAst.newBlock();

        // Map statements
        if (langBlock.getStatements() != null) {
            for (LangASTNode langStmt : langBlock.getStatements()) {
                // Use the existing map method to convert LangASTNode to JDT ASTNode
                ASTNode jdtNode = map(langStmt, jdtAst);

                // Check if the mapped node is a valid Statement
                if (jdtNode instanceof Statement) {
                    addStatement(block, (Statement) jdtNode);
                }
            }
        }

        return block;
    }

    /**
     * Maps a LangAssignment node to a JDT Assignment expression.
     * @param langAssignment The LangAssignment node to map
     * @param jdtAst The JDT AST to create nodes with
     * @return A JDT Assignment expression
     */
    public Assignment mapAssignment(LangAssignment langAssignment, AST jdtAst) {
        Assignment assignment = jdtAst.newAssignment();

        // Map the left side (target) of the assignment
        Expression leftSide = (Expression) map(langAssignment.getLeftSide(), jdtAst);
        assignment.setLeftHandSide(leftSide);

        // Map the right side (value) of the assignment
        Expression rightSide = (Expression) map(langAssignment.getRightSide(), jdtAst);
        assignment.setRightHandSide(rightSide);

        // Set the operator (=, +=, -=, etc.)
        Assignment.Operator operator = mapAssignmentOperator(langAssignment.getOperator());
        assignment.setOperator(operator);

        return assignment;
    }


}