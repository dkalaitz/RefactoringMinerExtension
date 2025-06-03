package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PyASTBuilder;
import antlr.ast.node.LangASTNode;
import antlr.ast.node.LangASTNodeFactory;
import antlr.ast.node.statement.LangBlock;

import java.util.ArrayList;

public class PyBaseASTBuilder {

    protected final PyASTBuilder mainBuilder;

    public PyBaseASTBuilder(PyASTBuilder mainBuilder) {
        this.mainBuilder = mainBuilder;
    }

}