package com.gymlab.api;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TreinoUsuarioRepository extends JpaRepository<TreinoUsuario, UUID> {
    List<TreinoUsuario> findByUserId(UUID userId); // Busca apenas os treinos do usuário dono do token
}