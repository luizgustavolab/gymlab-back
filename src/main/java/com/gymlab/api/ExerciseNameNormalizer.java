package com.gymlab.api;

public final class ExerciseNameNormalizer {

    private ExerciseNameNormalizer() {}

    public static String normalize(String nome) {

        if (nome == null) {
            return "";
        }

        return nome
                .toLowerCase()
                .replace("-", "")
                .replace("_", "")
                .replace("  ", " ")
                .trim();
    }
}