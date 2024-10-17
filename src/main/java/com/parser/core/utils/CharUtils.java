package com.parser.core.utils;

import java.util.Arrays;
import java.util.List;

public class CharUtils {
    private static final char NULL = 0;
    private static final char SIZE = 0x9FA5 + 1;

    private static final char[] LETTER_CHARS = buildLetterChars();
    private static final char[] LETTER_OR_DIGIT_CHARS = buildLetterOrDigitChars();
    private static final List<String> KEY_WORDS = Arrays.asList("IF", "NOT", "THEN", "ELSE");

    private static char[] buildLetterChars() {
        char[] ret = createCharArray();
        for (char i = 'a'; i <= 'z'; i++) {
            ret[i] = i;
        }
        for (char i = 'A'; i <= 'Z'; i++) {
            ret[i] = i;
        }
        ret['_'] = '_';
        ret['$'] = '$';
        return ret;
    }

    private static char[] createCharArray() {
        char[] ret = new char[SIZE];
        for (char i = 0; i < SIZE; i++) {
            ret[i] = NULL;
        }
        addChineseChar(ret);
        return ret;
    }

    /**
     * 添加中文字符，Unicode 编码范围：4E00-9FA5
     *
     * @param ret
     */
    private static void addChineseChar(char[] ret) {
        for (char i = 0x4E00; i < SIZE; i++) {
            ret[i] = i;
        }
    }

    private static char[] buildLetterOrDigitChars() {
        char[] ret = buildLetterChars();
        for (char i = '0'; i <= '9'; i++) {
            ret[i] = i;
        }
        return ret;
    }

    public static boolean isLetter(char c) {
        return c < SIZE && LETTER_CHARS[c] != NULL;
    }

    public static boolean isLetterOrDigit(char c) {
        return c < SIZE && LETTER_OR_DIGIT_CHARS[c] != NULL;
    }

    public static boolean isKeyWord(String k) {
        return KEY_WORDS.contains(k);
    }

    public static boolean isAnd(String k) {
        return "AND".equals(k);
    }

    public static boolean isOr(String k) {
        return "OR".equals(k);
    }

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

}
