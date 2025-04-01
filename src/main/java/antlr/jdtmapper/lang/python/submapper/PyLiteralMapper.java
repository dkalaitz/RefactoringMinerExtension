package antlr.jdtmapper.lang.python.submapper;

import antlr.ast.node.literal.LangStringLiteral;
import antlr.jdtmapper.lang.python.PyJdtASTMapper;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.StringLiteral;

public class PyLiteralMapper {

    /**
     * Maps a LangLiteral for string to a JDT StringLiteral node.
     *
     * @param langStringLiteral The LangLiteral node to map
     * @param jdtAst The JDT AST to create nodes with
     * @return A JDT StringLiteral node
     */
    public StringLiteral mapStringLiteral(LangStringLiteral langStringLiteral, AST jdtAst, PyJdtASTMapper pyJdtASTMapper) {
        if (langStringLiteral == null) return null;

        StringLiteral stringLiteral = jdtAst.newStringLiteral();
        stringLiteral.setLiteralValue(langStringLiteral.getValue());

       // stringLiteral.setSourceRange(langStringLiteral.getStartChar(), langStringLiteral.getLength());

        pyJdtASTMapper.setSourceRange(stringLiteral, langStringLiteral);

        return stringLiteral;
    }

}
