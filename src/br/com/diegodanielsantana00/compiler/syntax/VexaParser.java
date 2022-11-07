package br.com.diegodanielsantana00.compiler.syntax;

import br.com.diegodanielsantana00.compiler.exceptions.VexaSemanticException;
import br.com.diegodanielsantana00.compiler.exceptions.VexaSyntaxException;
import br.com.diegodanielsantana00.compiler.lexical.Token;
import br.com.diegodanielsantana00.compiler.lexical.VexaScanner;
import br.com.diegodanielsantana00.compiler.semantic.Semantic;

public class VexaParser {

    private VexaScanner scanner;
    private String varAtribui;
    private Token token;
    private int contadorSemantic;
    private boolean[] isInt;
    private int contadorEscopo;
    private Semantic[] Semantic;
    private int contadorAritmetico;

    public VexaParser(VexaScanner scanner) {
        this.scanner = scanner;
        Semantic = new Semantic[100];
        contadorSemantic = 0;
        contadorEscopo = 0;
        isInt = new boolean[100];
        contadorAritmetico = 0;
        varAtribui = null;
    }

    public void start() {
        checkINT();
        checkMain();
        startParmns();
        endParmns();
        bloco();
    }

    private void checkINT() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_RESERVED || token.getText().compareTo("int") != 0) {
            throw new VexaSyntaxException("INT expected!, found " + Token.TK_TEXT[token.getType()] + " ("
                    + token.getText() + ") at Line " + token.getLine() + " and column " + token.getColumn());
        }
    }

    private void checkMain() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_RESERVED || token.getText().compareTo("main") != 0) {
            throw new VexaSyntaxException("MAIN expected!, found " + Token.TK_TEXT[token.getType()] + " ("
                    + token.getText() + ") at Line " + token.getLine() + " and column " + token.getColumn());
        }
    }

    private void bloco() {
        startKey(); 
        contadorEscopo++;
        startKey();
        declaracaoVar();
        endKey();
        comando();
        endKey();
        contadorEscopo--;
    }

    private void declaracaoVar() {
        Semantic[contadorSemantic] = new Semantic();
        tipoIFC();
        identifier();
        semicolon();
        Semantic[contadorSemantic].setEscopo(contadorEscopo);
        contadorSemantic++;
        declarationVarLoop(); 
    }

    private void declarationVarLoop() {
        token = scanner.nextToken();
        if (token.getType() == Token.TK_RESERVED && (token.getText().compareTo("int") == 0
                || token.getText().compareTo("float") == 0 || token.getText().compareTo("char") == 0)) {
            Semantic[contadorSemantic] = new Semantic();
            Semantic[contadorSemantic].setType(token.getText());
            identifier(); 
            semicolon();
            Semantic[contadorSemantic].setEscopo(contadorEscopo);
            contadorSemantic++;
            declarationVarLoop();
        } else {
            scanner.back();
        }
    }

    private boolean isExists(String nome) {
        if (contadorSemantic == 0) {
            return false;
        }
        for (int i = 0; i < contadorSemantic; i++) {
            if (Semantic[i].getName().compareTo(nome) == 0 && Semantic[i].getEscopo() == contadorEscopo) {
                return true;
            }
        }
        return false;
    }

    private void tipoIFC() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_RESERVED || (token.getText().compareTo("int") != 0
                && token.getText().compareTo("float") != 0 && token.getText().compareTo("char") != 0)) {
            throw new VexaSyntaxException("INT OR FLOAT or CHAR expected!, found " + Token.TK_TEXT[token.getType()]
                    + " (" + token.getText() + ") at Line " + token.getLine() + " and column " + token.getColumn());
        }
        Semantic[contadorSemantic].setType(token.getText());
    }

    private void identifier() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_IDENTIFIER) {
            throw new VexaSyntaxException("IDENTIFIER expected!, found " + Token.TK_TEXT[token.getType()] + " ("
                    + token.getText() + ") at Line " + token.getLine() + " and column " + token.getColumn());
        }
        if (isExists(token.getText())) {
            throw new VexaSemanticException("Variable " + token.getText() + " already exists.");
        }
        Semantic[contadorSemantic].setName(token.getText());
    }

    private void atribuicao() {
        atribui();
        aritmetica();
        semicolon();
    }

    private void aritmetica() {
        numAritmetica();
        aritmeticaLoop();
        checkAritmetica();
    }

    private void aritmeticaLoop() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_SPECIAL && token.getType() != Token.TK_OPERATORRELA) {
            operadorAritmetico();
            token = scanner.nextToken();
            if (token.getType() == Token.TK_SPECIAL && token.getText().compareTo("(") == 0) {
                numAritmetica();
                aritmeticaLoop();
                endParmns();
            } else if (token.getType() == Token.TK_NUMBER || token.getType() == Token.TK_FLOAT) {
                if (token.getType() == Token.TK_NUMBER) {
                    isInt[contadorAritmetico] = true;
                    contadorAritmetico++;
                } else {
                    isInt[contadorAritmetico] = false;
                    contadorAritmetico++;
                }

                aritmeticaLoop();
            } else {
                scanner.back();
            }
        } else {
            scanner.back();
        }
    }

    private void checkAritmetica() {
        for (int i = 1; i < contadorAritmetico; i++) {
            if (isInt[i - 1] != isInt[i]) {
                throw new VexaSemanticException("Variable type is not supported for arithmetic expression.");
            }
        }

        if(varAtribui != null) {
            for(int i = 0; i < contadorSemantic; i++) {
                if(Semantic[i].getName().compareTo(varAtribui) == 0 && Semantic[i].getEscopo() == contadorEscopo) {
                    varAtribui = Semantic[i].getType();
                }
            }
            for (int i = 1; i < contadorAritmetico; i++) {
                if(varAtribui.compareTo("int") == 0) {
                    if(isInt[i] == false) {
                        throw new VexaSemanticException("Variable type int is not supported for arithmetic expression float.");
                    }
                } else {
                    if(isInt[i] == true) {
                        throw new VexaSemanticException("Variable type float is not supported for arithmetic expression int.");
                    }
                }
            }
        }
        contadorAritmetico = 0;
        varAtribui = null;
    }

    private void atribui() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_OPERATORARI) {
            throw new VexaSyntaxException("= expected!, found " + Token.TK_TEXT[token.getType()] + " (" + token.getText()
                    + ") at Line " + token.getLine() + " and column " + token.getColumn());
        }
    }

    private void semicolon() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_SPECIAL) {
            throw new VexaSyntaxException("; expected!, found " + Token.TK_TEXT[token.getType()] + " (" + token.getText()
                    + ") at Line " + token.getLine() + " and column " + token.getColumn());
        }
    }

    private void numAritmetica() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_IDENTIFIER && token.getType() != Token.TK_NUMBER
                && token.getType() != Token.TK_FLOAT) {
            throw new VexaSyntaxException("ID or NUMBER expected!, found " + Token.TK_TEXT[token.getType()] + " ("
                    + token.getText() + ") at Line " + token.getLine() + " and column " + token.getColumn());
        }

        if (token.getType() == Token.TK_NUMBER) {
            isInt[contadorAritmetico] = true;
            contadorAritmetico++;
        } else {
            isInt[contadorAritmetico] = false;
            contadorAritmetico++;
        }
    }

    private void operadorAritmetico() {
        if (token.getType() != Token.TK_OPERATORARI) {
            throw new VexaSyntaxException("Operator Expected!, found " + Token.TK_TEXT[token.getType()] + " ("
                    + token.getText() + ") at Line " + token.getLine() + " and column " + token.getColumn());
        }
    }

    private void comando() {
        token = scanner.nextToken();
        if (token.getType() == Token.TK_RESERVED && token.getText().compareTo("if") == 0) { // IF ELSE
            startParmns();
            relacional();
            endParmns();
            comando();
            comandoElse();
        } else if (token.getType() == Token.TK_IDENTIFIER) {
            if (!isExists(token.getText())) {
                throw new VexaSemanticException("Variable " + token.getText() + " doesn't exists.");
            }
            verificaAtribuicao(token.getText());
            varAtribui = token.getText();
            atribuicao();
        } else if (token.getType() == Token.TK_SPECIAL && token.getText().compareTo("{") == 0) {
            scanner.back();
            bloco();
        } else if (token.getType() == Token.TK_RESERVED && token.getText().compareTo("while") == 0) {
            iteracao();
        } else {
            scanner.back();
        }
    }

    private void verificaAtribuicao(String nome) {
        for (int i = 0; i < contadorSemantic; i++) {
            if (Semantic[i].getName().compareTo(nome) == 0 && Semantic[i].getEscopo() == contadorEscopo) {
                if (Semantic[i].getType().compareTo("char") == 0) {
                    throw new VexaSemanticException("Variable type " + token.getText() + " is not compatible.");
                }
            }
        }
    }

    private void palavraElse() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_RESERVED || token.getText().compareTo("else") != 0) {
            throw new VexaSyntaxException("ELSE expected!, found " + Token.TK_TEXT[token.getType()] + " ("
                    + token.getText() + ") at Line " + token.getLine() + " and column " + token.getColumn());
        }
    }

    private void startParmns() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_SPECIAL || token.getText().compareTo("(") != 0) {
            throw new VexaSyntaxException("Caracter Special expected!, found " + Token.TK_TEXT[token.getType()] + " ("
                    + token.getText() + ") at Line " + token.getLine() + " and column " + token.getColumn());
        }
    }

    private void endParmns() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_SPECIAL || token.getText().compareTo(")") != 0) {
            throw new VexaSyntaxException("Caracter Special expected!, found " + Token.TK_TEXT[token.getType()] + " ("
                    + token.getText() + ") at Line " + token.getLine() + " and column " + token.getColumn());
        }
    }

    private void startKey() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_SPECIAL || token.getText().compareTo("{") != 0) {
            throw new VexaSyntaxException("Caracter Special expected!, found " + Token.TK_TEXT[token.getType()] + " ("
                    + token.getText() + ") at Line " + token.getLine() + " and column " + token.getColumn());
        }
    }

    private void endKey() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_SPECIAL || token.getText().compareTo("}") != 0) {
            throw new VexaSyntaxException("Caracter Special expected!, found " + Token.TK_TEXT[token.getType()] + " ("
                    + token.getText() + ") at Line " + token.getLine() + " and column " + token.getColumn());
        }
    }

    private void relacional() {
        aritmetica();
        operadorRelacional();
        aritmetica();
    }

    private void operadorRelacional() {
        if (token.getType() != Token.TK_OPERATORRELA) {
            throw new VexaSyntaxException("Operator Relational expected!, found " + Token.TK_TEXT[token.getType()] + " ("
                    + token.getText() + ") at Line " + token.getLine() + " and column " + token.getColumn());
        }
    }

    private void comandoElse() {
        token = scanner.nextToken();
        if (token.getType() == Token.TK_SPECIAL && token.getText().compareTo("{") == 0) {
            palavraElse();
            comando();
            endKey();
        } else {
            scanner.back();
        }
    }

    private void iteracao() {
        startParmns();
        relacional();
        endParmns();
        comando();
    }
}