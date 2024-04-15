package br.ufscar.dc.compiladoras.analisadorLexico;

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

