/*

Trabalho 1 - CONSTRUÇÃO DE COMPILADORES

Integrantes do Grupo:
Arisa Abiko Sakaguti - 800357,
Matheus Ranzani - 800278,
Thales Winther - 802499

*/

package br.ufscar.dc.compiladores.analisadorLexico;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java Main <input file> <output file>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        Lexer lexer = new Lexer(inputFile);
        List<Token> tokens = lexer.tokenize();

        try (PrintWriter writer = new PrintWriter(outputFile)) {
            for (Token token : tokens) {
                writer.println(token);
            }
            if (!lexer.getErrors().isEmpty()) {
                for (String error : lexer.getErrors()) {
                    writer.println(error);
                }
            }
        }
    }
}

