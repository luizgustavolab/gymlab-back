package com.gymlab.api;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExercicioService {

    private final ExercicioRepository exercicioRepository;

    public ExercicioService(ExercicioRepository exercicioRepository) {
        this.exercicioRepository = exercicioRepository;
    }

    // Catálogo de exercícios é praticamente estático (sem endpoints de
    // escrita), então cacheia em memória para evitar buscar todas as
    // colunas (incluindo vários JSONB) do banco a cada geração/listagem.
    @Cacheable("exercicios")
    public List<Exercicio> listarTodos() {
        return exercicioRepository.findAll();
    }
}
