package com.gymlab.api;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TreinoUsuarioRepository
        extends JpaRepository<TreinoUsuario, UUID> {

    @EntityGraph(attributePaths = "exercicio")
    List<TreinoUsuario> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}