package br.ufscar.dc.compiladoras.analisadorLexico;

import java.io.*;
import java.util.*;

public class Lexer {
    private PushbackReader reader;
    private int currentChar;
    private boolean endOfFile = false;
    private int lineNumber = 1;
    private List<String> errors = new ArrayList<>();

    public List<String> getErrors() {
        return errors;
    }



    // Lista de palavras reservadas
    private static final Set<String> RESERVED_WORDS = new HashSet<>(Arrays.asList(
            "algoritmo", "declare", "leia", "escreva", "fim_algoritmo", "literal", "inteiro", "real",
            "logico", "e", "ou", "nao", "se", "senao", "fim_se", "entao", "caso", "seja", "fim_caso",
            "para", "ate", "faca", "fim_para", "enquanto", "fim_enquanto", "registro", "fim_registro",
            "tipo", "procedimento", "var", "fim_procedimento", "funcao", "retorne", "fim_funcao",
            "constante", "falso", "verdadeiro"
    ));

    public Lexer(String filePath) throws IOException {
        reader = new PushbackReader(new FileReader(filePath), 2);  // Capacidade para ".."
        advance();
    }

    private void advance() throws IOException {
        currentChar = reader.read();
        if (currentChar == '\n') {
            lineNumber++;
        }
        if (currentChar == -1) {
            endOfFile = true;
        }
    }


    public List<Token> tokenize() throws IOException {
        List<Token> tokens = new ArrayList<>();
        while (!endOfFile) {
            while (Character.isWhitespace(currentChar)) {
                advance();
            }

            if (Character.isLetter(currentChar)) {
                tokens.add(word());
            } else if (Character.isDigit(currentChar)) {
                tokens.add(number());
            } else if (currentChar == '.') {
                tokens.add(handleDot());
                advance();
            } else if (currentChar == '"') {
                tokens.add(stringLiteral());
            } else if (currentChar == '{') {
                skipComment();
            } else if ("+-*/(),;:%^&[]".indexOf(currentChar) != -1) {
                tokens.add(new Token(String.valueOf((char) currentChar), String.valueOf((char) currentChar)));
                advance();
            } else if (currentChar == '<' || currentChar == '>' || currentChar == '!') {
                processComparisonOperators(tokens);
            } else if (currentChar == '=') {
                processEquals(tokens);
            } else {
                errors.add("Linha " + lineNumber + ": " + (char) currentChar + " - simbolo nao identificado");
                return tokens;
            }
        }
        return tokens;
    }


    private void processComparisonOperators(List<Token> tokens) throws IOException {
        char currentOperator = (char) currentChar;
        advance();
        if (currentChar == '=' && (currentOperator == '>' || currentOperator == '<' || currentOperator == '!')) {
            tokens.add(new Token(currentOperator + "=", currentOperator + "="));
            advance();
        } else if (currentOperator == '<' && currentChar == '-') {
            tokens.add(new Token("<-", "<-"));
            advance();
        } else if (currentOperator == '<' && currentChar == '>') {
            tokens.add(new Token("<>", "<>"));
            advance();
        } else {
            tokens.add(new Token(String.valueOf(currentOperator), String.valueOf(currentOperator)));
        }
    }

    private void processEquals(List<Token> tokens) throws IOException {
        advance();
        if (currentChar == '=') {
            tokens.add(new Token("==", "=="));
            advance();
        } else {
            tokens.add(new Token("=", "="));
        }
    }

    private Token word() throws IOException {
        StringBuilder builder = new StringBuilder();
        while (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
            builder.append((char) currentChar);
            advance();
        }
        String word = builder.toString();
        if (RESERVED_WORDS.contains(word.toLowerCase())) {
            return new Token(word.toLowerCase(), word.toLowerCase()); // Convert to lowercase for consistency
        } else {
            return new Token("IDENT", word); // Treat as identifier
        }
    }

    private Token number() throws IOException {
        StringBuilder builder = new StringBuilder();
        boolean isReal = false;
        while (Character.isDigit(currentChar) || (currentChar == '.' && !isReal)) {
            if (currentChar == '.') {
                int lookahead = reader.read();
                if (Character.isDigit(lookahead)) {
                    builder.append((char) currentChar);
                    builder.append((char) lookahead);
                    isReal = true;
                    advance();  // Atualiza currentChar após adicionar lookahead
                } else {
                    reader.unread(lookahead);  // Devolve lookahead para o stream
                    break;
                }
            } else {
                builder.append((char) currentChar);
            }
            advance();
        }
        if (isReal) {
            return new Token("NUM_REAL", builder.toString());
        } else {
            return new Token("NUM_INT", builder.toString());
        }
    }

    private Token handleDot() throws IOException {
        advance();
        if (currentChar == '.') {
            advance();
            return new Token("..", "..");
        } else {
            // Retorna erro porque '.' foi encontrado em um contexto que não esperávamos
            reader.unread(currentChar);
            return new Token(".", ".");
        }
    }

    private void skipComment() throws IOException {
        while (currentChar != '}' && !endOfFile) {
            advance();
        }
        if (currentChar == '}') {
            advance(); // Move past the closing brace.
        }
    }

    private Token stringLiteral() throws IOException {
        StringBuilder builder = new StringBuilder();
        advance(); // Começa depois da aspa inicial
        while (currentChar != '"' && !endOfFile) {
            builder.append((char) currentChar);
            advance();
        }
        advance(); // Move past the closing quote
        return new Token("CADEIA", "\"" + builder.toString() + "\"");
    }

}
