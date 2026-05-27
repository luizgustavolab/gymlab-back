package com.gymlab.api;

public record TreinoDashboardDto(
    String diaSemana,
    String grupoMuscular,
    String exercicioNome,
    String equipamento,
    String instrucao,
    int series,
    int repeticoes,
    String intervalo
) {}