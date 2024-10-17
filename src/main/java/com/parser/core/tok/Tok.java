package com.parser.core.tok;

import com.parser.core.constants.Symbol;


public class Tok {

    public final Symbol symbol;

    private final String value;

    public Tok(Symbol symbol) {
        if (symbol == null) {
        }
        this.symbol = symbol;
        this.value = symbol.value();
    }

    public Tok(Symbol symbol, String value) {
        if (symbol == null || value == null) {
            throw new IllegalArgumentException("symbol and value can not be null");
        }
        this.symbol = symbol;
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("{ symbol : %s, value : %s }", symbol.value(), value);
    }

}
