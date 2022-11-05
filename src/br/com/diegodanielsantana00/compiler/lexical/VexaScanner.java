package br.com.diegodanielsantana00.compiler.lexical;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import br.com.diegodanielsantana00.compiler.exceptions.VexaLexicalException;

public class VexaScanner {

	private char[] content;
	private int estado;
	private int pos;
	private int line;
	private int column;

	public VexaScanner(String filename) {
		try {
			line = 1;
			column = 0;
			String txtConteudo;
			txtConteudo = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
			content = txtConteudo.toCharArray();
			pos = 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Token nextToken() {
		char currentChar;
		Token token;
		String term = "";
		boolean doubleBool = true;
		boolean charAux = false;
		if (isEndFile()) {
			return null;
		}
		estado = 0;
		while (true) {
			currentChar = nextChar();
			column++;

			switch (estado) {
				case 0:
					if (isCaracteres(currentChar)) {
						term += currentChar;
						token = new Token();
						token.setType(Token.TK_CARACTER);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						return token;
					} else if (isChar(currentChar)) {
						term += currentChar;
						estado = 1;
					} else if (isDigit(currentChar)) {
						estado = 2;
						term += currentChar;
					} else if (isSpace(currentChar)) {
						estado = 0;
					} else if (isOperatorRela(currentChar)) {
						estado = 3;
						term += currentChar;
					} else if(isOperatorAri(currentChar)){
						term += currentChar;
						token = new Token();
						token.setType(Token.TK_OPERATORARI);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						return token;
					} else if(isAspasD(currentChar)){
						estado = 4;
						term += currentChar;
					} else if(isAspasS(currentChar)){
						estado = 5;
						term += currentChar;
					} else {
						throw new VexaLexicalException("Simbolo incorreto Linha " + (line-1) + " Coluna " + column + "--->  '" + term + currentChar +"' ");
					}
					break;
				case 1:
					if (isVar(term)) {
						term += currentChar;
						if (!isEndFile(currentChar))
							back();
						token = new Token();
						token.setType(Token.TK_VAR);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						return token;
					} else if (isChar(currentChar) || isDigit(currentChar)) {
						estado = 1;
						term += currentChar;
					} else if (isSpace(currentChar) || isEndFile(currentChar) || isParenthesesFuction(currentChar)) {
						if (!isEndFile(currentChar) || isParenthesesFuction(currentChar))
							back();
						token = new Token();
						token.setType(Token.TK_IDENTIFIER);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						return token;
					} else {
						throw new VexaLexicalException("Malformed Identifier");
					}
					break;
				case 2:
					if (isDigit(currentChar)) {
						estado = 2;
						term += currentChar;
					}else if(currentChar == '.') {
						doubleBool = true;
						estado = 2;
						term += currentChar;
					} else if (!isChar(currentChar) || isEndFile(currentChar)) {
						if (!isEndFile(currentChar))
							back();
						token = new Token();
						token.setType(doubleBool ? Token.TK_DOUBLE : Token.TK_NUMBER);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						return token;
					} else {
						throw new VexaLexicalException("Erro: número float inválido Linha " + (line-1) + " Coluna " + column + "--->  '" + term + currentChar +"' ");
					}
					break;
				case 3:
					if (isOperatorAtri(currentChar)) {
						term += currentChar;
						if (!isEndFile(currentChar)) {
							back();
						}
						token = new Token();
						token.setType(Token.TK_OPERATORRELA);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						return token;
					}else if(term.equals("=")  && (isSpace(currentChar) || isEndFile())){
						if (!isEndFile(currentChar)) {
							back();
						}
						token = new Token();
						token.setType(Token.TK_OPERATORATRI);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						return token;
					} else {
						throw new VexaLexicalException("Error: Linha " + (line-1) + " Coluna " + column + "--->  '" + term + currentChar + "' ");
					}
					
				case 4:
					if (isAspasD(currentChar)) {
						term += currentChar;
						token = new Token();
						token.setType(Token.TK_STRING);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						return token;
					}else if(isDigit(currentChar) || isChar(currentChar)){
						estado = 4;
						term += currentChar;
					} else {
						throw new VexaLexicalException("Error Lexico: (String não fechada com aspas duplas) Linha " + (line-1) + " Coluna " + column + "--->  '" + term + currentChar + "' ");
					}
					break;
				case 5:
					if (isAspasS(currentChar)) {
						term += currentChar;
						token = new Token();
						token.setType(Token.TK_CHAR);
						token.setText(term);
						token.setLine(line);
						token.setColumn(column - term.length());
						return token;
					}else if((isDigit(currentChar) || isChar(currentChar)) && !charAux){
						estado = 5;
						term += currentChar;
						charAux = true;
					} else {
						throw new VexaLexicalException("Error Lexico: (Char invalido) Linha " + (line-1) + " Coluna " + column + "--->  '" + term + currentChar + "' ");
					}
					break;
			}
		}

	}

	private boolean isVar(String var) { // Palavra reservada
		String[] varAux = { "while", "int", "double", "float", "main", "if", "else" };
		return Arrays.stream(varAux).anyMatch(var::equals);
	}

	private boolean isCaracteres(char c) { // Caracter Especial
		return c == ')' || c == '(' || c == '{' || c == '}' || c == ';' || c == ':' || c == ';' || c == ',';
	}

	private boolean isDigit(char c) { // Digito
		return c >= '0' && c <= '9';
	}

	private boolean isChar(char c) { // Letra
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private boolean isOperatorRela(char c) { // Operador Relacional
		return c == '>' || c == '<' || c == '=';
	}

	private boolean isOperatorAri(char c) { // Operador aritmético
		return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
	}

	private boolean isOperatorAtri(char c) { // Operador atribuição
		return c == '=';
	}

	private boolean isParenthesesFuction(char c) { // Abrir function
		return c == '(';
	}

	private boolean isSpace(char c) { // Espaço em branco
		if (c == '\n' || c == '\r') {
			line++;
			column = 0;
		}
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}

	private boolean isAspasD(char c) { // Aspas simples
		return c == '"' ;
	}

	private boolean isAspasS(char c) { // Aspas Duplas
		return c == '\'' ;
	}

	private char nextChar() {
		if (isEndFile()) {
			return '\0';
		}
		return content[pos++];
	}

	private boolean isEndFile() {
		return pos >= content.length;
	}

	private boolean isEndFile(char c) {
		return c == '\0';
	}

	private void back() {
		pos--;
		column--;
	}

}
