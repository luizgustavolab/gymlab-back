package com.gymlab.api;

public record GrupoConfig(
        String grupo,
        int quantidadeExercicios,
        int series,
        int repeticoes,
        String intervalo
) {
}