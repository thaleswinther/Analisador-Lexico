/*

Trabalho 1 - CONSTRUÇÃO DE COMPILADORES

Integrantes do Grupo:
Arisa Abiko Sakaguti - 800357,
Matheus Ranzani - 800278,
Thales Winther - 802499

*/

package br.ufscar.dc.compiladores.analisadorLexico;

public class Token {
    private String type;
    private String value;

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }
    @Override
    public String toString() {
        if (!type.equals(value)) {
            return "<'" + this.value + "'," + type + ">";
        } else {
            return "<'" + this.value + "','" + this.type + "'>";
        }
    }

    public String getValue() {
        return value;
    }
}
