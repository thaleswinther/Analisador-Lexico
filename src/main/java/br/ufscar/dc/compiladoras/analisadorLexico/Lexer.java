package br.ufscar.dc.compiladoras.analisadorLexico;

import java.io.*;
import java.util.*;

public class Lexer {
    private BufferedReader reader;
    private int currentChar;
    private boolean endOfFile = false;

    // Lista de palavras reservadas
    private static final Set<String> RESERVED_WORDS = new HashSet<>(Arrays.asList(
            "algoritmo", "declare", "leia", "escreva", "fim_algoritmo", "literal", "inteiro", "real",
            "logico", "e", "ou", "nao", "se", "senao", "fim_se", "entao", "caso", "seja", "fim_caso"
    ));

    public Lexer(String filePath) throws IOException {
        reader = new BufferedReader(new FileReader(filePath));
        advance();
    }

    private void advance() throws IOException {
        currentChar = reader.read();
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
            } else if (currentChar == '"') {
                tokens.add(stringLiteral());
            } else if (currentChar == '{') {
                skipComment();
            } else if (currentChar == '<' || currentChar == '>' || currentChar == '!') {
                char currentOperator = (char) currentChar;
                advance();
                if (currentChar == '=' && (currentOperator == '>' || currentOperator == '<' || currentOperator == '!')) {
                    tokens.add(new Token(currentOperator + "=", currentOperator + "="));
                    advance();
                } else if (currentOperator == '<' && currentChar == '-') {
                    tokens.add(new Token("<-", "<-"));
                    advance();
                } else {
                    tokens.add(new Token(String.valueOf(currentOperator), String.valueOf(currentOperator)));
                }
            } else if (currentChar == '=') {
                advance();
                if (currentChar == '=') {
                    tokens.add(new Token("==", "=="));
                    advance();
                } else {
                    tokens.add(new Token("=", "="));
                }
            } else if ("+-*/(),;:".indexOf(currentChar) != -1) {
                if (currentChar == '-' && !tokens.isEmpty() && tokens.get(tokens.size() - 1).getValue().equals("<")) {
                    tokens.remove(tokens.size() - 1);  // Remove the '<' token
                    tokens.add(new Token("<-", "<-"));  // Add '<-' token
                } else {
                    tokens.add(new Token(String.valueOf((char) currentChar), String.valueOf((char) currentChar)));
                }
                advance();
            }

        }
        return tokens;
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
        while (Character.isDigit(currentChar)) {
            builder.append((char) currentChar);
            advance();
        }
        return new Token("NUM_INT", builder.toString());
    }

    private Token handleDot() throws IOException {
        advance(); // Avança após o primeiro '.'
        if (currentChar == '.') {
            advance(); // Avança após o segundo '.', confirmando que é um operador de intervalo
            return new Token("..", "..");
        } else {
            // Se não for um '..', então trate como um erro ou parte de um número real, conforme necessário
            // Para simplificar, vamos assumir que esse caso não ocorrerá sem um dígito antes, então retornamos apenas um ponto
            return new Token(".", ".");
        }
    }

    private Token stringLiteral() throws IOException {
        StringBuilder builder = new StringBuilder();
        advance(); // Start after the initial double quote
        while (currentChar != '"' && !endOfFile) {
            builder.append((char) currentChar);
            advance();
        }
        advance(); // Move past the closing quote
        return new Token("CADEIA", "\"" + builder.toString() + "\"");
    }

    private void skipComment() throws IOException {
        while (currentChar != '}' && !endOfFile) {
            advance();
        }
        if (currentChar == '}') {
            advance(); // Skip the closing brace
        }
    }
}