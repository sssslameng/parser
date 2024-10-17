package com.parser.core.tok;

import com.parser.core.constants.Symbol;
import com.parser.core.utils.CharUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class TokParser {

    static final Pattern DOUBLE_QUOTES_PATTERN = Pattern.compile("\\\\\"");
    static final Pattern SINGLE_QUOTES_PATTERN = Pattern.compile("\\\\'");

    static final char EOF = (char) -1;

    private char[] buf;
    private List<Tok> tokes = new ArrayList<>();
    private int failCount = 0;

    private int forward = 0;
    private int leftPos = 0;

    public TokParser(String content) {
        char[] contentChar = content.toCharArray();
        int contentCharLength = contentChar.length;
        int len = contentCharLength + 1;
        buf = new char[len];
        buf[len - 1] = EOF;
        System.arraycopy(contentChar, 0, buf, 0, contentCharLength);
    }

    public List<Tok> parse() {
        scan();
        return tokes;
    }

    char next() {
        return buf[++forward];
    }

    char peek() {
        return buf[forward];
    }

    public void scan() {
        while (peek() != EOF) {
            if (scanId()) {
                continue;
            }
            if (scanOperator()) {
                continue;
            }
            if (scanString()) {
                continue;
            }
            if (scanNumber()) {
                continue;
            }
            if (scanSpace()) {
                continue;
            }
        }
    }

    boolean scanId() {
        if (!CharUtils.isLetter(peek())) {
            return false;
        }

        while (CharUtils.isLetterOrDigit(next())) {
            ;
        }

        String id = new String(subBuf(leftPos, forward - 1));
        if ("true".equals(id)) {
            addTok(new Tok(Symbol.TRUE, id));
        } else if ("OR".equals(id)) {
            addTok(new Tok(Symbol.OR, id));
        } else if ("AND".equals(id)) {
            addTok(new Tok(Symbol.AND, id));
        } else {
            addTok(new Tok(Symbol.ID, id));
        }
        return prepareNextScan();
    }

    boolean scanOperator() {
        Tok tok;
        // + - * / % ++ --
        switch (peek()) {
            case '+':
                tok = new Tok(Symbol.ADD);
                next();
                return ok(tok);
            case '-':
                tok = new Tok(Symbol.SUB);
                next();
                return ok(tok);
            case '*':
                tok = new Tok(Symbol.MUL);
                next();
                return ok(tok);
            case '/':
                tok = new Tok(Symbol.DIV);
                next();
                return ok(tok);
            case '%':
                tok = new Tok(Symbol.MOD);
                next();
                return ok(tok);
            case '&':
                tok = new Tok(Symbol.ACC);
                next();
                return ok(tok);
            case '=':
                if (next() == '=') {
                    tok = new Tok(Symbol.EQUAL);
                    next();
                } else {
                    tok = new Tok(Symbol.ASSIGN);
                }
                return ok(tok);
            case '!':
                if (next() == '=') {
                    tok = new Tok(Symbol.NOTEQUAL);
                    next();
                } else {
                    throw new RuntimeException("不支持的操作符：!");
                }
                return ok(tok);
            case '<':
                char ch = next();
                if (ch == '=') {
                    tok = new Tok(Symbol.LE);
                    next();
                } else if (ch == '>') {
                    tok = new Tok(Symbol.NOTEQUAL2);
                    next();
                } else {
                    tok = new Tok(Symbol.LT);
                }
                return ok(tok);
            case '>':
                if (next() == '=') {
                    tok = new Tok(Symbol.GE);
                    next();
                } else {
                    tok = new Tok(Symbol.GT);
                }
                return ok(tok);
            // ( ) [ ] { }
            case '(':
                tok = new Tok(Symbol.L_PAREN);
                next();
                return ok(tok);
            case ')':
                tok = new Tok(Symbol.R_PAREN);
                next();
                return ok(tok);
            case '[':
                tok = new Tok(Symbol.L_BRACKET);
                next();
                return ok(tok);
            case ']':
                tok = new Tok(Symbol.R_BRACKET);
                next();
                return ok(tok);
            case '{':
                tok = new Tok(Symbol.L_BRACE);
                next();
                return ok(tok);
            case '}':
                tok = new Tok(Symbol.R_BRACE);
                next();
                return ok(tok);
            case ',':
                tok = new Tok(Symbol.COMMA);
                next();
                return ok(tok);
            case '@':
                tok = new Tok(Symbol.EXTRA);
                next();
                return ok(tok);
            default:
                return fail();
        }
    }

    boolean scanString() {

        char quotes = peek();
        if (quotes != '"' && quotes != '\'') {
            return fail();
        }

        for (char c = next(); true; c = next()) {
            if (c == quotes) {
                char[] sb = subBuf(leftPos + 1, forward - 1);
                String str;
                if (sb != null) {
                    if (quotes == '"') {
                        str = DOUBLE_QUOTES_PATTERN.matcher(new String(sb)).replaceAll("\"");
                    } else {
                        str = SINGLE_QUOTES_PATTERN.matcher(new String(sb)).replaceAll("'");
                    }
                    if (str.length() == 0) {
                        str = String.valueOf(quotes) + quotes;
                    }
                } else {
                    str = String.valueOf(quotes) + quotes;
                }

                Tok tok = new Tok(Symbol.STR, str);
                addTok(tok);
                next();
                return prepareNextScan();
            }

            if (c == EOF) {
                throw new RuntimeException("公式错误，字符参数不完整");
            }
        }
    }

    boolean scanNumber() {
        char c = peek();
        if (!CharUtils.isDigit(c)) {
            return fail();
        }
        int numStart = leftPos;
        c = skipDecimalDigit();
        Symbol symbol = null;
        if (c == '.') {
            // 浮点型
            symbol = Symbol.DOUBLE;
            next();
            skipDecimalDigit();
        }
        if (c == '~') {
            symbol = Symbol.DOMAIN;
            next();
            skipDecimalDigit();
        }
        if (symbol == null) {
            symbol = Symbol.INT;
        }
        char[] num = subBuf(numStart, forward - 1);

        Tok tok = new Tok(symbol, new String(num));
        addTok(tok);
        return prepareNextScan();
    }

    boolean scanSpace() {
        char c = peek();
        if (c != ' ') {
            return fail();
        }
        // 处理多个空格
        c = next();
        for (; c == ' '; ) {
            c = next();
        }
        addTok(new Tok(Symbol.SPACE));
        return prepareNextScan();
    }

    char skipDecimalDigit() {
        char c = peek();
        for (; CharUtils.isDigit(c); ) {
            c = next();
        }
        return c;
    }

    boolean prepareNextScan() {
        leftPos = forward;
        // 重置计数
        this.failCount = 0;
        return true;
    }

    boolean ok(Tok tok) {
        tokes.add(tok);
        return prepareNextScan();
    }

    boolean fail() {
        forward = leftPos;
        this.failCount++;
        if (this.failCount >= 5) {
            throw new RuntimeException("公式解析异常");
        }
        return false;
    }

    void addTok(Tok tok) {
        tokes.add(tok);
    }

    char[] subBuf(int start, int end) {
        if (start > end) {
            return null;
        }
        int rl = end - start + 1;
        char[] res = new char[rl];
        for (int i = 0; i < rl; i++) {
            res[i] = buf[start + i];
        }
        return res;
    }

    public static void main(String[] args) {
        List<Tok> toks = new TokParser("IF B_BLX=2 THEN [*,*]<=0{1~8,27~29,31}{1}").parse();
        for (Tok tok : toks) {
            System.out.print(tok.value());
        }
    }

}
