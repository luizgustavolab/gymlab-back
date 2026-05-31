package com.gymlab.api;

import com.gymlab.api.Genero;
import com.gymlab.api.ObjetivoTreino;

import java.util.List;
import java.util.UUID;

public class WorkoutContext {

    private UUID userId;
    private Genero genero;
    private ObjetivoTreino objetivo;
    private int diasPorSemana;
    private List<Exercicio> exercicios;

    public WorkoutContext() {}

    public WorkoutContext(UUID userId,
                          Genero genero,
                          ObjetivoTreino objetivo,
                          int diasPorSemana,
                          List<Exercicio> exercicios) {
        this.userId = userId;
        this.genero = genero;
        this.objetivo = objetivo;
        this.diasPorSemana = diasPorSemana;
        this.exercicios = exercicios;
    }

    public UUID getUserId() { return userId; }

    public Genero getGenero() { return genero; }

    public ObjetivoTreino getObjetivo() { return objetivo; }

    public int getDiasPorSemana() { return diasPorSemana; }

    public List<Exercicio> getExercicios() { return exercicios; }
}