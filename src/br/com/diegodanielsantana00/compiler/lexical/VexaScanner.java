package br.com.diegodanielsantana00.compiler.lexical;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import br.com.diegodanielsantana00.compiler.exceptions.VexaLexicalException;

public class VexaScanner {

	private char[] content;
	private int state;
	private int pos;
	private boolean debugVar;
	private int line;
	private int column;

	public VexaScanner(String filename, boolean debugVar) {
		try {
			this.debugVar = debugVar;
			column = 0;
			line = 1;
			String auxCode = "";
			auxCode = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
			if (this.debugVar) {
				System.out.println("-- ENTRADA --");
				System.out.println(auxCode);
				System.out.println("-------------");
			}
			content = auxCode.toCharArray();
			pos = 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Token nextToken() {
		char currentChar;
		Token token = null;
		String term = "";
		if (isEOF()) {
			return null;
		}

		state = 0;
		while (pos < content.length) {
			if (isEOF()) {
				return null;
			}
			currentChar = nextChar();
			column++;

			switch (state) {
				case 0:
					if (isChar(currentChar)) {
						term += currentChar;
						state = 1;
					} else if (isSpace(currentChar)) {
						state = 0;
					} else if (isSpecial(currentChar)) {
						term += currentChar;
						token = new Token();
						token.setType(Token.TK_SPECIAL);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						if (debugVar) {
							System.out.println(token.toString());
						}
						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;
					} else if (isPrivate(currentChar)) {
						term += currentChar;
						state = 5;
					} else if (isConditional(currentChar)) {
						term += currentChar;
						token = new Token();
						token.setType(Token.TK_IF_ELSE);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;
					} else if (isArithmeticOperator(currentChar)) {
						if (currentChar == '=') {
							term += currentChar;
							state = 3;
							break;
						}
						term += currentChar;
						token = new Token();
						token.setType(Token.TK_OPERATORARI);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;
					} else if (isLong(currentChar)) {
						term += currentChar;
						token = new Token();
						token.setType(Token.TK_LONG);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;
					} else if (isPow(currentChar)) {
						term += currentChar;
						token = new Token();
						token.setType(Token.TK_POW);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;
					} else if (isRelationalOperator(currentChar)) {
						term += currentChar;
						state = 4;
					} else if (currentChar == '!') {
						term += currentChar;
						state = 8;
					} else if (isDigit(currentChar)) {
						term += currentChar;
						state = 9;
					} else if (isBreak(currentChar)) {
						term += currentChar;
						token = new Token();
						token.setType(Token.TK_BREAK);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;
					} else if (isComentInLine(currentChar)) {
						term += currentChar;
						state = 11;
					} else if (isComentVariousLines(currentChar)) {
						term += currentChar;
						state = 12;
					} else if (isCaracter(String.valueOf(currentChar))) {
						term += currentChar;
						state = 13;
					} else if (isFinal(currentChar)) {
						term += currentChar;
						token = new Token();
						token.setType(Token.TK_FINAL);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;

					} else {
						throw new VexaLexicalException("Simbolo mal construido");
					}
					break;
				case 1:
					if (isChar(currentChar) || isDigit(currentChar) || isUnderline(currentChar)) {
						state = 1;
						term += currentChar;
					} else if (isSpace(currentChar) || isArithmeticOperator(currentChar)) {
						state = 2;
					} else if (isPontoVirgula(currentChar)) {
						token = new Token();
						token.setType(Token.TK_IDENTIFIER);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						back();
						return token;
					} else {
						throw new VexaLexicalException("Identificador mal formado '" + currentChar + "'");
					}
					break;
				case 2:
					token = new Token();
					if (isReserved(term)) {
						token.setType(Token.TK_RESERVED);
					} else {
						token.setType(Token.TK_IDENTIFIER);
					}
					token.setText(term);
					token.setLine(line);
					token.setColumn(column - term.length());
					back();
					if (debugVar) {
						System.out.println(token.toString());
					}
					return token;
				case 3:
					if (currentChar == '=') {
						term += currentChar;
						token = new Token();
						token.setType(Token.TK_OPERATORRELA);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());

						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;
					} else {
						token = new Token();
						token.setType(Token.TK_OPERATORARI);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						back();
						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;
					}
				case 4:
					if (currentChar == '=') {
						term += currentChar;
						token = new Token();
						token.setType(Token.TK_OPERATORARI);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;
					} else {
						token = new Token();
						token.setType(Token.TK_OPERATORRELA);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;
					}
				case 5:
					if (isChar(currentChar)) {
						term += currentChar;
						state = 6;
					} else {
						throw new VexaLexicalException("Identificador PRIVADO mal formado");
					}
					break;
				case 6:
					if (isChar(currentChar) || isDigit(currentChar)) {
						state = 6;
						term += currentChar;
					} else if (isSpace(currentChar) || isArithmeticOperator(currentChar)) {
						state = 7;
					} else {
						throw new VexaLexicalException("Identificador PRIVADO mal formado");
					}
					break;
				case 7:
					token = new Token();
					token.setType(Token.TK_PRIVATE);
					token.setText(term);
					token.setLine(line);
					token.setColumn(column - term.length());
					back();
					if (debugVar) {
						System.out.println(token.toString());
					}
					return token;
				case 8:
					if (currentChar == '=') {
						term += currentChar;
						token = new Token();
						token.setType(Token.TK_OPERATORRELA);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						back();
						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;
					} else {
						throw new VexaLexicalException("Operador mal construido");
					}
				case 9:
					if (isDigit(currentChar)) {
						state = 9;
						term += currentChar;
					} else if (!isChar(currentChar) && !isDot(currentChar)) {
						token = new Token();
						token.setType(Token.TK_NUMBER);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						back();
						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;
					} else if (isDot(currentChar)) {
						state = 15;
						term += currentChar;
					} else {
						throw new VexaLexicalException("Número Inteiro mal construido");
					}
					break;
				case 10:
					if (isDigit(currentChar)) {
						state = 10;
						term += currentChar;
					} else if (!isChar(currentChar)) {
						token = new Token();
						token.setType(Token.TK_FLOAT);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;
					} else {
						throw new VexaLexicalException("Número Float mal construido");
					}
					break;
				case 11:
					if (isComentInLine(currentChar) || isDigit(currentChar) || isChar(currentChar)
							|| isOperator(currentChar) || isPrivate(currentChar) || isConditional(currentChar)
							|| isUnderline(currentChar)) {
						state = 11;
						term += currentChar;
					} else if (isSpace(currentChar)) {
						token = new Token();
						token.setType(Token.TK_COMMENT);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;
					} else {
						throw new VexaLexicalException("Comentário de linha mal construido");
					}
					break;
				case 12:
					if (isSpace(currentChar) || isDigit(currentChar) || isChar(currentChar) || isOperator(currentChar)
							|| isPrivate(currentChar) || isConditional(currentChar) || isUnderline(currentChar)) {
						state = 12;
						term += currentChar;
					} else if (isComentVariousLines(currentChar)) {
						term += currentChar;
						token = new Token();
						token.setType(Token.TK_COMMENT);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;
					} else {
						throw new VexaLexicalException("Comentário de Parágrafo mal construido");
					}
					break;
				case 13:
					if (isDigit(currentChar) || isChar(currentChar)) {
						term += currentChar;
						state = 14;
					} else {
						throw new VexaLexicalException("Char mal construido");
					}
					break;
				case 14:
					if (isCaracter(String.valueOf(currentChar))) {
						term += currentChar;
						token = new Token();
						token.setType(Token.TK_CHAR);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						if (debugVar) {
							System.out.println(token.toString());
						}
						return token;
					} else {
						throw new VexaLexicalException("Char mal construido");
					}
				case 15:
					if (isDigit(currentChar)) {
						state = 10;
						term += currentChar;
					} else {
						throw new VexaLexicalException("Número Float mal construido");
					}
					break;
			}
		}
		return token;
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private boolean isChar(char c) {
		return (c >= 'a' && c <= 'z');
	}

	private boolean isSpace(char c) {
		if (c == '\n' || c == '\r') {
			line++;
			column = 0;
		}
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}

	private boolean isReserved(String c) {
		if (c.compareTo("main") == 0 || c.compareTo("if") == 0 || c.compareTo("else") == 0 || c.compareTo("while") == 0
				|| c.compareTo("double") == 0 || c.compareTo("for") == 0 || c.compareTo("int") == 0
				|| c.compareTo("float") == 0 || c.compareTo("char") == 0 || c.compareTo("vexa") == 0
				|| c.compareTo("vexateste123") == 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isSpecial(char c) {
		return c == ')' || c == '(' || c == '{' || c == '}' || c == ',' || c == ';';
	}

	private boolean isPrivate(char c) {
		return c == '#';
	}

	private boolean isPontoVirgula(char c) {
		return c == ';';
	}

	private boolean isConditional(char c) {
		return c == '?' || c == ':';
	}

	private boolean isArithmeticOperator(char c) {
		return c == '+' || c == '-' || c == '=' || c == '*' || c == '/';
	}

	private boolean isRelationalOperator(char c) {
		return c == '>' || c == '<';
	}

	private boolean isLong(char c) {
		return c == '@';
	}

	private boolean isPow(char c) {
		return c == '^';

	}

	private boolean isComentInLine(char c) {
		return c == '`';
	}

	private boolean isComentVariousLines(char c) {
		return (c == '~');
	}

	private boolean isOperator(char c) {
		return c == '>' || c == '<' || c == '=' || c == '!';
	}

	private boolean isCaracter(String c) {
		return (c.compareTo("'") == 0);
	}

	private boolean isUnderline(char c) {
		return c == '_';
	}

	private boolean isFinal(char c) {
		return c == '$';
	}

	private boolean isDot(char c) {
		return c == '.';
	}

	private boolean isBreak(char c) {
		return c == '&';
	}

	private char nextChar() {
		return content[pos++];
	}

	public void back() {
		pos--;
	}

	public void back(int time) {
		for (int i = 0; i < time; i++) {
			pos--;
		}
	}

	private boolean isEOF() {
		return pos == content.length;
	}

}