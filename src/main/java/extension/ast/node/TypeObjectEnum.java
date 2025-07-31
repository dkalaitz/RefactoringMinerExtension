package extension.ast.node;

public enum TypeObjectEnum {
    OBJECT("Object"),
    INT("int"),
    BOOLEAN("boolean"),
    STRING("String"),
    DOUBLE("double"),
    FLOAT("float"),
    LONG("long"),
    CHAR("char"),
    BYTE("byte"),
    SHORT("short");

    private final String name;

    public String getName() {
        return name;
    }

    TypeObjectEnum(String name) {
        this.name = name;
    }

    /**
     * Returns the type name formatted with angle brackets for UML representation
     * @return Type name wrapped in angle brackets
     */
    public String getFormatTypeWithAngleBrackets
    () {
        return "<" + name + ">";
    }

}
