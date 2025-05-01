package antlr.ast.node;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class PositionUtils {

    public static PositionInfo getPositionInfo(ParserRuleContext ctx) {
        if (ctx == null) {
            return new PositionInfo(-1, -1, -1, -1, -1, -1);
        }
        return new PositionInfo(
                getStartLine(ctx),
                getEndLine(ctx),
                getStartChar(ctx),
                getEndChar(ctx),
                getStartColumn(ctx),
                getEndColumn(ctx)
        );
    }

    public static int getStartLine(ParserRuleContext ctx) {
        Token start = (ctx == null) ? null : ctx.getStart();
        return (start != null) ? start.getLine() : -1;
    }

    public static int getEndLine(ParserRuleContext ctx) {
        Token stop = (ctx == null) ? null : ctx.getStop();
        return (stop != null) ? stop.getLine() : -1;
    }

    public static int getStartChar(ParserRuleContext ctx) {
        Token start = (ctx == null) ? null : ctx.getStart();
        return (start != null) ? start.getStartIndex() : -1;
    }

    public static int getEndChar(ParserRuleContext ctx) {
        Token stop = (ctx == null) ? null : ctx.getStop();
        return (stop != null) ? stop.getStopIndex() : -1;
    }

    public static int getStartColumn(ParserRuleContext ctx) {
        Token start = (ctx == null) ? null : ctx.getStart();
        return (start != null) ? start.getCharPositionInLine() : -1;
    }

    public static int getEndColumn(ParserRuleContext ctx) {
        Token stop = (ctx == null) ? null : ctx.getStop();
        return (stop != null) ? stop.getCharPositionInLine() : -1;
    }
}
