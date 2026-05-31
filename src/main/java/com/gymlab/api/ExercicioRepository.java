package com.gymlab.api;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExercicioRepository
        extends JpaRepository<Exercicio, UUID> {

    Optional<Exercicio> findByExternalId(String externalId);

}