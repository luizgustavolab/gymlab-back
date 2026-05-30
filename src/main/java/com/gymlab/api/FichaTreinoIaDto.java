package com.gymlab.api;
public record FichaTreinoIaDto(

    String exercicioExternalId,

    String grupoMuscular,

    int series,

    int repeticoes,

    String intervalo,

    String diaSemana

) {}