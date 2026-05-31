package com.gymlab.api;

import java.util.Set;

public final class CategoriaTreinoRules {

    private CategoriaTreinoRules() {}

    public static final Set<String> HIPERTROFIA = Set.of(
            "Musculação",
            "Powerlifting"
    );

    public static final Set<String> FORCA = Set.of(
            "Musculação",
            "Powerlifting",
            "olympic_weightlifting",
            "Strongman"
    );

    public static final Set<String> EMAGRECIMENTO = Set.of(
            "Musculação",
            "Condicionamento",
            "Pliometria",
            "Calistenia"
    );

    public static final Set<String> DEFINICAO = Set.of(
            "Musculação",
            "Condicionamento",
            "Calistenia"
    );

    public static boolean categoriaValida(ObjetivoTreino objetivo, String categoria) {

        if (objetivo == null || categoria == null) {
            return false;
        }

        String cat = categoria.trim();

        return switch (objetivo) {
            case HIPERTROFIA -> HIPERTROFIA.contains(cat);
            case FORCA -> FORCA.contains(cat);
            case EMAGRECIMENTO -> EMAGRECIMENTO.contains(cat);
            case DEFINICAO -> DEFINICAO.contains(cat);
        };
    }
}