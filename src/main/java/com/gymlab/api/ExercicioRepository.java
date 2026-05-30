package com.gymlab.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExercicioRepository extends JpaRepository<Exercicio, UUID> {

    Optional<Exercicio> findByExternalId(String externalId);

    @Query(value = "SELECT * FROM exercicios " +
                   "WHERE (:nivel IS NULL OR nivel = :nivel) " +
                   "AND (:categoria IS NULL OR categoria = :categoria) " +
                   "AND (:musculo IS NULL OR CAST(musculos_primarios AS text) LIKE '%' || :musculo || '%') " +
                   "ORDER BY RANDOM() LIMIT :limit",
           nativeQuery = true)
    List<Exercicio> findFilteredPool(
        @Param("nivel") String nivel,
        @Param("categoria") String categoria,
        @Param("musculo") String musculo,
        @Param("limit") int limit
    );
}