package org.glavo.rfg;

public enum Generator {
    Zero,
    Random;

    public static Generator fromString(String s) {
        return switch (s) {
            case "z", "zero" -> Zero;
            case "r", "random" -> Random;
            default -> null;
        };
    }
}
