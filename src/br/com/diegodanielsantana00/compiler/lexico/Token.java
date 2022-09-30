package br.com.diegodanielsantana00.compiler.lexico;

public class Token {
	public static final int TK_IDENTIFIER = 0;
	public static final int TK_NUMBER = 1;
	public static final int TK_OPERATORRELA = 2;
	public static final int TK_DOUBLE = 3;
	public static final int TK_RETURN = 4;
	public static final int TK_VAR = 5;
	public static final int TK_CARACTER = 6;
	public static final int TK_OPERATORARI = 7;
	public static final int TK_OPERATORATRI = 8;
	public static final int TK_STRING= 9;
	public static final int TK_CHAR= 10;


	public static final String TK_TEXT[] = {
			"IDENTIFIER", "NUMBER", "OPERATORRELA", "DOUBLE", "RETURN", "VAR", "CARACTER", "OPERATORARI", "OPERATORATRI", "STRING", "CHAR"
	};

	private int line;
	private String text;
	private int type;
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

	public void setType(int type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "<" + TK_TEXT[type] + ", " + text + ">"; // Geração do token
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

}
