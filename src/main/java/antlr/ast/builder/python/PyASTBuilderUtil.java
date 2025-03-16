package antlr.ast.builder.python;

import antlr.base.python.Python3Parser;

public class PyASTBuilderUtil {

    /**
     * Helper method to extract the operator between expressions.
     * This is just an example; the actual implementation depends on your parser grammar.
     */
    public static String extractOperator(Python3Parser.ExprContext ctx) {

        if (ctx.getChildCount() == 3) {
            return ctx.getChild(1).getText();
        }

        if (ctx.getChildCount() > 3) {
            for (int i = 0; i < ctx.getChildCount(); i++) {
                String text = ctx.getChild(i).getText();
                if (isOperator(text)) {
                    return text;
                }
            }

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
