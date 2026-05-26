package com.gymlab.api;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ExercicioRepository extends JpaRepository<Exercicio, UUID> {
    // Pronto. Métodos como save(), findAll(), findById() já existem aqui sem digitar nenhuma linha.
}