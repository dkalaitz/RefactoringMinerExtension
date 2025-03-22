package antlr.jdtmapper.lang.python.submapper;

import antlr.ast.node.expression.LangAssignment;
import antlr.ast.node.expression.LangInfixExpression;
import antlr.ast.node.expression.LangSimpleName;
import antlr.jdtmapper.lang.python.PyJdtASTMapper;
import org.eclipse.jdt.core.dom.*;

public class PyExpressionMapper {

    /**
     * Maps a LangSimpleName node to a JDT SimpleName node.
     *
     * @param langSimpleName The LangSimpleName node to map
     * @param jdtAst The JDT AST to create nodes with
     * @return A JDT SimpleName node
     */
    public SimpleName mapSimpleName(LangSimpleName langSimpleName, AST jdtAst) {
        if (langSimpleName == null) return null;

        // Create a new SimpleName node with the identifier from the LangSimpleName
        return jdtAst.newSimpleName(langSimpleName.getIdentifier());
    }

    /**
     * Maps a LangInfixExpression node to a JDT InfixExpression node.
     *
     * @param langInfix The LangInfixExpression node to map
     * @param jdtAst The JDT AST to create nodes with
     * @param pyJdtASTMapper Reference to the main mapper for delegating child node mapping
     * @return A JDT InfixExpression node
     */
    public InfixExpression mapInfixExpression(LangInfixExpression langInfix, AST jdtAst, PyJdtASTMapper pyJdtASTMapper) {
        if (langInfix == null) return null;

        InfixExpression infixExpression = jdtAst.newInfixExpression();

        // Map left operand
        Expression leftOperand = (Expression) pyJdtASTMapper.map(langInfix.getLeft(), jdtAst);
        infixExpression.setLeftOperand(leftOperand);

        // Map operator
        InfixExpression.Operator operator = mapInfixOperator(langInfix.getOperator());
        infixExpression.setOperator(operator);

        // Map right operand
        Expression rightOperand = (Expression) pyJdtASTMapper.map(langInfix.getRight(), jdtAst);
        infixExpression.setRightOperand(rightOperand);

        return infixExpression;
    }

    /**
     * Maps a LangAssignment node to a JDT Assignment expression.
     *
     * @param langAssignment The LangAssignment node to map
     * @param jdtAst The JDT AST to create nodes with
     * @param pyJdtASTMapper Reference to the main mapper for delegating child node mapping
     * @return A JDT Assignment expression
     */
    public Assignment mapAssignment(LangAssignment langAssignment, AST jdtAst, PyJdtASTMapper pyJdtASTMapper) {
        if (langAssignment == null) return null;

        Assignment assignment = jdtAst.newAssignment();

        // Map the left side (target) of the assignment
        Expression leftSide = (Expression) pyJdtASTMapper.map(langAssignment.getLeftSide(), jdtAst);
        assignment.setLeftHandSide(leftSide);

        // Map the right side (value) of the assignment
        Expression rightSide = (Expression) pyJdtASTMapper.map(langAssignment.getRightSide(), jdtAst);
        assignment.setRightHandSide(rightSide);

        // Set the operator (=, +=, -=, etc.)
        Assignment.Operator operator = mapAssignmentOperator(langAssignment.getOperator());
        assignment.setOperator(operator);


        assignment.setSourceRange(langAssignment.getStartChar(), langAssignment.getLength());

        return assignment;
    }

    /**
     * Maps a Lang assignment operator to a JDT Assignment.Operator.
     *
     * @param langOperator The Lang assignment operator to map
     * @return A JDT Assignment.Operator
     */
    private Assignment.Operator mapAssignmentOperator(String langOperator) {
        // Default to simple assignment if operator is null
        if (langOperator == null) {
            return Assignment.Operator.ASSIGN;
        }

        // Map operators from Python to Java
        return switch (langOperator) {
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
            default -> Assignment.Operator.ASSIGN; // Default to = for any other operator
        };
    }

    /**
     * Maps a Python operator string to a JDT InfixExpression.Operator.
     *
     * @param pythonOperator The Python operator string
     * @return The corresponding JDT operator
     */
    private InfixExpression.Operator mapInfixOperator(String pythonOperator) {
        return switch (pythonOperator) {
            // Arithmetic operators
            case "+" -> InfixExpression.Operator.PLUS;
            case "-" -> InfixExpression.Operator.MINUS;
            case "*" -> InfixExpression.Operator.TIMES;
            case "/" -> InfixExpression.Operator.DIVIDE;
            case "%" -> InfixExpression.Operator.REMAINDER;

            // Comparison operators
            case "==" -> InfixExpression.Operator.EQUALS;
            case "!=" -> InfixExpression.Operator.NOT_EQUALS;
            case "<" -> InfixExpression.Operator.LESS;
            case "<=" -> InfixExpression.Operator.LESS_EQUALS;
            case ">" -> InfixExpression.Operator.GREATER;
            case ">=" -> InfixExpression.Operator.GREATER_EQUALS;

            // Logical operators
            case "and" -> InfixExpression.Operator.CONDITIONAL_AND;
            case "or" -> InfixExpression.Operator.CONDITIONAL_OR;

            // Bitwise operators
            case "&" -> InfixExpression.Operator.AND;
            case "|" -> InfixExpression.Operator.OR;
            case "^" -> InfixExpression.Operator.XOR;
            case "<<" -> InfixExpression.Operator.LEFT_SHIFT;
            case ">>" -> InfixExpression.Operator.RIGHT_SHIFT_SIGNED;

            // Python-specific operators that need special handling
            case "//" -> {
                // Integer division in Python - no direct equivalent in Java
                // Using normal division as fallback
                yield InfixExpression.Operator.DIVIDE;
            }
            case "**" -> {
                // Exponentiation in Python - no direct equivalent in JDT InfixExpression.Operator
                // This would typically be mapped to Math.pow() in Java, but for now we'll use TIMES
                // as a fallback (this is a limitation we should document)
                yield InfixExpression.Operator.TIMES;
            }
            case "is", "is not", "in", "not in" -> {
                // Python identity and membership operators - no direct equivalents
                // For "is", we could use EQUALS as an approximation
                // For "is not", we could use NOT_EQUALS
                // For "in"/"not in", we might need custom code generation
                yield pythonOperator.contains("not") ?
                        InfixExpression.Operator.NOT_EQUALS :
                        InfixExpression.Operator.EQUALS;
            }
            default -> {
                // Default to PLUS as a fallback, but log a warning
                System.err.println("Warning: Unsupported Python operator '" + pythonOperator +
                        "' - using PLUS as default");
                yield InfixExpression.Operator.PLUS;
            }
        };
    }


}
