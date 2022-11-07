package br.com.diegodanielsantana00.compiler.lexical;

public class Token {

    public static final int TK_NUMBER = 0;
    public static final int TK_FLOAT = 1;
    public static final int TK_IDENTIFIER = 2;
    public static final int TK_DOUBLE = 3;
    public static final int TK_OPERATORRELA = 4;
    public static final int TK_OPERATORARI = 5;
    public static final int TK_SPECIAL = 6;
    public static final int TK_RESERVED = 7;
    public static final int TK_PRIVATE = 8;
    public static final int TK_IF_ELSE = 9;
    public static final int TK_LONG = 10;
    public static final int TK_POW = 11;
    public static final int TK_BREAK = 12;
    public static final int TK_FINAL = 13;
    public static final int TK_COMMENT = 14;
    public static final int TK_CHAR = 15;

    public static final String TK_TEXT[] = {
            "NUMBER",
            "FLOAT",
            "IDENTIFIER",
            "DOUBLE",
            "OPERATORRELA",
            "OPERATORARI",
            "SPECIAL",
            "RESERVED",
            "PRIVATE",
            "IF_ELSE",
            "LONG",
            "POW",
            "BREAK",
            "FINAL",
            "COMMENT",
            "CHAR",
    };

    private int type;
    private String text;

    private int line;
    private int column;

    public Token(int type, String text) {
        super();
        this.type = type;
        this.text = text;
    }

    public Token() {
        super();
    }

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String toString() {
        return "<" + TK_TEXT[type] + ", " + text + ">";
    }
}
