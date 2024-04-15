package br.ufscar.dc.compiladoras.analisadorLexico;

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
