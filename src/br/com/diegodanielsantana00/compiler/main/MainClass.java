package br.com.diegodanielsantana00.compiler.main;

import br.com.diegodanielsantana00.compiler.exceptions.VexaLexicalException;
import br.com.diegodanielsantana00.compiler.exceptions.VexaSemanticException;
import br.com.diegodanielsantana00.compiler.exceptions.VexaSyntaxException;
import br.com.diegodanielsantana00.compiler.lexical.VexaScanner;
import br.com.diegodanielsantana00.compiler.syntax.VexaParser;

public class MainClass {
    public static void main(String[] args) {
        try {
            VexaScanner scanner = new VexaScanner("/Users/diegodaniel/Documents/Compiler/input.vexa");
            VexaParser parser = new VexaParser(scanner);

            parser.start();
            System.out.println("Compilado com sucesso");
        } catch (VexaLexicalException ex) {
            System.err.println("Erro Léxico: " + ex.getMessage());
        } catch (VexaSyntaxException ex) {
            System.out.println("Erro Sintático: " + ex.getMessage());
        } catch (VexaSemanticException ex) {
            System.out.println("Erro Semântico: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Erro Genérico: " + ex.getMessage());
        }
    }
}
