package br.com.diegodanielsantana00.compiler.syntax;

import br.com.diegodanielsantana00.compiler.exceptions.VexaSemanticException;
import br.com.diegodanielsantana00.compiler.exceptions.VexaSyntaxException;
import br.com.diegodanielsantana00.compiler.lexical.Token;
import br.com.diegodanielsantana00.compiler.lexical.VexaScanner;
import br.com.diegodanielsantana00.compiler.semantic.Semantic;

public class VexaParser {

    private String varAtribui;
    private VexaScanner scanner;
    private Token token;
    private boolean[] isInt;
    private int contadorAritmetico;
    private int contadorSemantic;
    private Semantic[] Semantic;
    private int contadorEscopo;

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
            throw new VexaSyntaxException(
                    "INT incorreto!, encontrado a inconsistência " + Token.TK_TEXT[token.getType()] + " ("
                            + token.getText() + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
        }
    }

    private void checkMain() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_RESERVED || token.getText().compareTo("main") != 0) {
            throw new VexaSyntaxException(
                    "MAIN incorreto!, encontrado a inconsistência " + Token.TK_TEXT[token.getType()] + " ("
                            + token.getText() + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
        }
    }

    private void bloco() {
        startKey();
        contadorEscopo++;
        // startKey();
        declaracaoVar();
        if (token.getType() == Token.TK_RESERVED) {
            scanner.back(token.getText().length());
            command();
        }
        endKey();
        command();
        // endKey();
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
                || token.getText().compareTo("float") == 0 || token.getText().compareTo("char") == 0
                || token.getText().compareTo("double") == 0)) {
            Semantic[contadorSemantic] = new Semantic();
            Semantic[contadorSemantic].setType(token.getText());
            identifier();
            semicolon();
            Semantic[contadorSemantic].setEscopo(contadorEscopo);
            contadorSemantic++;
            declarationVarLoop();
        } else {
            if (token.getType() == Token.TK_FINAL) {
                scanner.back();
                return;
            }
            scanner.back();
        }
    }

    private boolean isExists(String nome) {
        if (contadorSemantic == 0) {
            return false;
        }
        for (int i = 0; i < contadorSemantic; i++) {
            if (Semantic[i].getName().compareTo(nome) == 0 && Semantic[i].getEscopo() <= contadorEscopo) {
                return true;
            }
        }
        return false;
    }

    private void tipoIFC() {
        token = scanner.nextToken();
        if (isExists(token.getText())) {
            scanner.back(token.getText().length());
        } else if (token.getType() != Token.TK_RESERVED || (token.getText().compareTo("int") != 0
                && token.getText().compareTo("float") != 0 && token.getText().compareTo("char") != 0
                && token.getText().compareTo("double") != 0)) {
            throw new VexaSyntaxException("INT ou FLOAT ou CHAR incorreto!, encontrado a inconsistência "
                    + Token.TK_TEXT[token.getType()]
                    + " (" + token.getText() + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
        }

        Semantic[contadorSemantic].setType(token.getText());
    }

    private void identifier() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_IDENTIFIER) {
            throw new VexaSyntaxException(
                    "IDENTIFIER incorreto!, encontrado a inconsistência " + Token.TK_TEXT[token.getType()] + " ("
                            + token.getText() + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
        }
        if (isExists(token.getText())) {
            throw new VexaSemanticException("Variavel " + token.getText() + " já existe.");
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
                throw new VexaSemanticException("O tipo da variável não é suportado para expressão aritmética");
            }
        }

        if (varAtribui != null) {
            for (int i = 0; i < contadorSemantic; i++) {
                if (Semantic[i].getName().compareTo(varAtribui) == 0 && Semantic[i].getEscopo() == contadorEscopo) {
                    varAtribui = Semantic[i].getType();
                }
            }
            for (int i = 1; i < contadorAritmetico; i++) {
                if (varAtribui.compareTo("int") == 0) {
                    if (isInt[i] == false) {
                        throw new VexaSemanticException(
                                "O tipo da variavel não é suportado para float");
                    }
                } else {
                    if (isInt[i] == true) {
                        throw new VexaSemanticException(
                                "o tipo da variavel não é suportado para int");
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
            throw new VexaSyntaxException(
                    "'=' incorreto!, encontrado a inconsistência " + Token.TK_TEXT[token.getType()] + " ("
                            + token.getText()
                            + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
        }
    }

    private void semicolon() {
        token = scanner.nextToken();
        if (token.getType() == Token.TK_OPERATORARI) {
            token = scanner.nextToken();
            if (token.getType() == Token.TK_FLOAT || token.getType() == Token.TK_NUMBER
                    || token.getType() == Token.TK_DOUBLE || token.getType() == Token.TK_CHAR) {
                token = scanner.nextToken();
                if (token.getType() == Token.TK_SPECIAL || token.getType() == Token.TK_OPERATORARI) {
                    if (token.getType() == Token.TK_OPERATORARI) {
                        token = scanner.nextToken();
                        if (token.getType() == Token.TK_FLOAT || token.getType() == Token.TK_NUMBER
                        || token.getType() == Token.TK_DOUBLE || token.getType() == Token.TK_CHAR) {
                            token = scanner.nextToken();
                            if (token.getType() == Token.TK_SPECIAL) {
                                
                            }else{
                                throw new VexaSyntaxException(
                            "requer um ';'!, encontrado a inconsistência " + Token.TK_TEXT[token.getType()] + " ("
                                    + token.getText()
                                    + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
                            }
                        }else{
                            throw new VexaSyntaxException(
                            "requer um ';'!, encontrado a inconsistência " + Token.TK_TEXT[token.getType()] + " ("
                                    + token.getText()
                                    + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
                        }
                    }
                } else {
                    throw new VexaSyntaxException(
                            "requer um ';'!, encontrado a inconsistência " + Token.TK_TEXT[token.getType()] + " ("
                                    + token.getText()
                                    + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
                }
            } else {
                throw new VexaSyntaxException(
                        "';' incorreto!, encontrado a inconsistência " + Token.TK_TEXT[token.getType()] + " ("
                                + token.getText()
                                + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
            }
        } else if (token.getType() == Token.TK_SPECIAL) {
            throw new VexaSyntaxException(
                    "';' incorreto!, encontrado a inconsistência " + Token.TK_TEXT[token.getType()] + " ("
                            + token.getText()
                            + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
        }
        // if (token.getType() == Token.TK_SPECIAL) {
        // throw new VexaSyntaxException(
        // "';' incorreto!, encontrado a inconsistência " +
        // Token.TK_TEXT[token.getType()] + " ("
        // + token.getText()
        // + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
        // }
    }

    private void numAritmetica() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_IDENTIFIER && token.getType() != Token.TK_NUMBER
                && token.getType() != Token.TK_FLOAT) {
            throw new VexaSyntaxException("VARIAVEL RESERVADA ou NUMBER incorreto!, encontrado a inconsistência "
                    + Token.TK_TEXT[token.getType()] + " ("
                    + token.getText() + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
        }

        if (token.getType() == Token.TK_NUMBER) {
            isInt[contadorAritmetico] = true;
            contadorAritmetico++;
        } else {
            isInt[contadorAritmetico] = false;
            contadorAritmetico++;
        }
    }

    private void command() {
        token = scanner.nextToken();
        if (token == null) {
            return;
        }

        if (token.getType() == Token.TK_RESERVED && token.getText().compareTo("if") == 0) {
            startParmns();
            relacional();
            endParmns();
            command();
            commandElse();
        } else if (token.getType() == Token.TK_IDENTIFIER) {
            if (!isExists(token.getText())) {
                throw new VexaSemanticException("Variavel " + token.getText() + " não existe");
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
                    throw new VexaSemanticException("Variavel do tipo " + token.getText() + " não é compativel");
                }
            }
        }
    }

    private void palavraElse() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_RESERVED || token.getText().compareTo("else") != 0) {
            throw new VexaSyntaxException(
                    "ELSE incorreto!, encontrado a inconsistência " + Token.TK_TEXT[token.getType()] + " ("
                            + token.getText() + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
        }
    }

    private void startParmns() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_SPECIAL || token.getText().compareTo("(") != 0) {
            throw new VexaSyntaxException(
                    "Caracter Special incorreto!, encontrado a inconsistência " + Token.TK_TEXT[token.getType()] + " ("
                            + token.getText() + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
        }
    }

    private void startKey() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_SPECIAL || token.getText().compareTo("{") != 0) {
            throw new VexaSyntaxException(
                    "Caracter Special incorreto!, encontrado a inconsistência " + Token.TK_TEXT[token.getType()] + " ("
                            + token.getText() + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
        }
    }

    private void endKey() {
        token = scanner.nextToken();
        if (token.getType() == Token.TK_FINAL || token.getType() == Token.TK_OPERATORARI) {
            return;
        }
        if (token.getType() != Token.TK_SPECIAL || token.getText().compareTo("}") != 0) {
            throw new VexaSyntaxException(
                    "Caracter Special incorreto!, encontrado a inconsistência " + Token.TK_TEXT[token.getType()] + " ("
                            + token.getText() + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
        }
    }

    private void operadorAritmetico() {
        if (token.getType() != Token.TK_OPERATORARI) {
            throw new VexaSyntaxException(
                    "Operator incorreto!, encontrado a inconsistência " + Token.TK_TEXT[token.getType()] + " ("
                            + token.getText() + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
        }
    }

    private void relacional() {
        aritmetica();
        operadorRelacional();
        aritmetica();
    }

    private void operadorRelacional() {
        if (token.getType() != Token.TK_OPERATORRELA) {
            throw new VexaSyntaxException(
                    "Operador Relacional!, encontrado a inconsistência " + Token.TK_TEXT[token.getType()] + " ("
                            + token.getText() + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
        }
    }

    private void commandElse() {
        token = scanner.nextToken();
        if (token.getType() == Token.TK_SPECIAL && token.getText().compareTo("{") == 0) {
            palavraElse();
            command();
            endKey();
        } else {
            scanner.back();
        }
    }

    private void endParmns() {
        token = scanner.nextToken();
        if (token.getType() != Token.TK_SPECIAL || token.getText().compareTo(")") != 0) {
            throw new VexaSyntaxException(
                    "Caracter Special incorreto!, encontrado a inconsistência " + Token.TK_TEXT[token.getType()] + " ("
                            + token.getText() + ") na linha " + token.getLine() + " e coluna " + token.getColumn());
        }
    }

    private void iteracao() {
        startParmns();
        relacional();
        endParmns();
        command();
    }
}