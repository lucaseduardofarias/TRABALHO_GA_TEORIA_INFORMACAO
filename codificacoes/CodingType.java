package codificacoes;

import java.util.Arrays;

public enum CodingType {
    Golomb(0,"Golomb"),
    EliasGamma(1,"Elias-Gamma"),
    Fibonacci(2,"Fibonacci"),
    Unary(3,"Unaria"),
    Delta(4,"Delta");

    private final String name;
    private final int identifier;

    CodingType(int identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }

    public int getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public static CodingType getValueByName(String name) {
        return Arrays.stream(CodingType.values())
                .filter(codingType -> codingType.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("CodingType not found: " + name));
    }
}
