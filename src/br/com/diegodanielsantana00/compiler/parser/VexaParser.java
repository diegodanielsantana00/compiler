package br.com.diegodanielsantana00.compiler.parser;

import br.com.diegodanielsantana00.compiler.lexical.Token;
import br.com.diegodanielsantana00.compiler.lexical.VexaScanner;

public class VexaParser {

	private VexaScanner scanner;
	private Token token;
	private boolean printTokens;

	public VexaParser(VexaScanner scanner, boolean printTokens) {
		this.scanner = scanner;
		this.printTokens = printTokens;
	}

	public void Compiler() {
		T();
		El();

	}

	public void El() {
		token = scanner.nextToken();
		if (printTokens && token != null) {
			System.out.println(token);
		}
		if (token != null) {
			T();
			El();
		}
	}

	public void T() {
		token = scanner.nextToken();
		if (printTokens && token != null) {
			System.out.println(token);
		}
	}

}
