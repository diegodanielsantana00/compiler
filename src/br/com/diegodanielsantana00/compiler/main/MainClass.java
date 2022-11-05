package br.com.diegodanielsantana00.compiler.main;
import br.com.diegodanielsantana00.compiler.exceptions.VexaLexicalException;
import br.com.diegodanielsantana00.compiler.exceptions.VexaSemanticException;
import br.com.diegodanielsantana00.compiler.lexical.VexaScanner;
import br.com.diegodanielsantana00.compiler.parser.VexaParser;

public class MainClass {
	public static void main(String[] args) {
		try {
			boolean printTokens = true;
			VexaScanner auxScanner = new VexaScanner("input.vexa");
			VexaParser  auxParser = new VexaParser(auxScanner, printTokens);
			auxParser.Compiler();
			System.out.println("Compilação completa!");
		} catch (VexaLexicalException ex) {
			System.out.println("Erro Semântico: " + ex.getMessage());
		} catch (VexaSemanticException ex) {
            System.out.println("Erro Semântico: " + ex.getMessage());
        } 
		catch (Exception ex) {
			System.out.println("Error generico (Java)");
			System.out.println(ex.getClass().getName());
		}
	}
}

