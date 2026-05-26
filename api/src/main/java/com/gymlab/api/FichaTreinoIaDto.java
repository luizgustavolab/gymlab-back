package com.gymlab.api;

public record FichaTreinoIaDto(
    String exercicioNome,
    String grupoMuscular,
    int series,
    int repeticoes,
    String intervalo,
    String diaSemana
) {}