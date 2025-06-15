package antlr.ast.builder.python.component;

import antlr.ast.builder.python.PyASTBuilder;

public class PyBaseASTBuilder {

    protected final PyASTBuilder mainBuilder;

    public PyBaseASTBuilder(PyASTBuilder mainBuilder) {
        this.mainBuilder = mainBuilder;
    }

}