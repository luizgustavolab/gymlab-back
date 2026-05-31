package com.gymlab.api;

import java.util.Map;

public final class VolumeConfig {

    private VolumeConfig() {
    }

    private static final Map<String, Integer> EXERCICIOS_POR_GRUPO =
            Map.ofEntries(

                    Map.entry("PEITO", 4),
                    Map.entry("COSTAS", 4),
                    Map.entry("QUADRICEPS", 4),

                    Map.entry("POSTERIOR", 3),
                    Map.entry("GLUTEOS", 3),
                    Map.entry("OMBROS", 3),

                    Map.entry("BICEPS", 3),
                    Map.entry("TRICEPS", 3),
                    Map.entry("PANTURRILHAS", 2),
                    Map.entry("CORE", 3),

                    Map.entry("TRAPEZIO", 1),
                    Map.entry("ADUTORES", 1),
                    Map.entry("ANTEBRACO", 2),
                    Map.entry("LOMBAR", 1),

                    Map.entry("PESCOCO", 1)
            );

    public static int exercicios(String grupo) {

        if (grupo == null || grupo.isBlank()) {
            return 3;
        }

        return EXERCICIOS_POR_GRUPO.getOrDefault(
                grupo.toUpperCase(),
                3
        );
    }
}