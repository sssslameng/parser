package com.parser.core.constants;

public enum Symbol {
    ASSIGN("="),

    COLON(":"),

    L_PAREN("("), R_PAREN(")"),

    L_BRACKET("["), R_BRACKET("]"),

    L_BRACE("{"), R_BRACE("}"),

    ADD("+"), SUB("-"),

    MUL("*"), DIV("/"),

    MOD("%"),

    EQUAL("=="), NOTEQUAL("!="), NOTEQUAL2("<>"),

    LT("<"), LE("<="), GT(">"), GE(">="),

    IF("IF"), NOT("NOT"), THEN("THEN"), ELSE("ELSE"), AND("AND"), OR("OR"),

    QUESTION("?"),

    ACC("&"),

    COMMA(","),

    SPACE(" "),

    ID("ID"),

    NODE("NODE"),

    STR("STR"), TRUE("TRUE"), FALSE("FALSE"),

    INT("INT"), DOUBLE("DOUBLE"),

    DOMAIN("DOMAIN"),

    EXTRA("@"),

    EOF("EOF");

    private final String value;

    private Symbol(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
