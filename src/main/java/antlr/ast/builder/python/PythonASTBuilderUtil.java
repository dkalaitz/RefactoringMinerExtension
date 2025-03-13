package antlr.ast.builder.python;

import antlr.base.python.Python3Parser;

public class PythonASTBuilderUtil {

    /**
     * Helper method to extract the operator between expressions.
     * This is just an example; the actual implementation depends on your parser grammar.
     */
    public static String extractOperator(Python3Parser.ExprContext ctx) {
        // Simple case: if there are exactly 3 children, then
        // child(0) is the left expr, child(1) is the operator, child(2) is the right expr.
        if (ctx.getChildCount() == 3) {
            return ctx.getChild(1).getText();
        }

        // Handle more complex expressions with multiple children
        if (ctx.getChildCount() > 3) {
            // Option 1: Look for operator tokens based on their type
            for (int i = 0; i < ctx.getChildCount(); i++) {
                // Identify operator based on token type or text pattern
                // This depends on your specific grammar
                String text = ctx.getChild(i).getText();
                if (isOperator(text)) {
                    return text;
                }
            }

            // Option 2: Return multiple operators as a list or concatenated string
            StringBuilder operators = new StringBuilder();
            for (int i = 0; i < ctx.getChildCount(); i++) {
                String text = ctx.getChild(i).getText();
                if (isOperator(text)) {
                    if (!operators.isEmpty()) {
                        operators.append(",");
                    }
                    operators.append(text);
                }
            }

            if (!operators.isEmpty()) {
                return operators.toString();
            }
        }

        System.out.println("No operator found, returning null");
        return null;
    }

    // Helper method to identify operators
    private static boolean isOperator(String text) {
        String[] operators = {
                // Arithmetic
                "+", "-", "*", "/", "//", "%", "**",
                // Comparison
                ">", "<", ">=", "<=", "==", "!=", "<>",
                // Logical
                "and", "or", "not",
                // Bitwise
                "&", "|", "^", "~", "<<", ">>",
                // Membership
                "in", "not in",
                // Identity
                "is", "is not"
        };

        for (String op : operators) {
            if (text.equals(op)) {
                return true;
            }
        }
        return false;

    }


}
